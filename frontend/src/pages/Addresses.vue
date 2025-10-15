<script setup>
import Table from "@/components/table.vue";
import Header from "@/components/header.vue";
import Button from "@/components/button.vue";

import {markRaw, onMounted, onBeforeUnmount, reactive, ref, watch} from "vue";
import Pagination from "@/components/pagination.vue";
import axios from "@/axios.js";

import EditIcon from '@/assets/editIcon.svg?component'
import DeleteIcon from '@/assets/deleteIcon.svg?component'
import DetailsPopup from "@/components/details-popup.vue";
import FormCreate from "@/components/forms/form-create.vue";
import Input from "@/components/input.vue";
import Select from "@/components/select.vue";
import FormUpdate from "@/components/forms/form-update.vue";

// ----------------- состояние -----------------
const page = ref(1);
const totalPages = ref(1);
const rows = reactive([]);
const popup = reactive({visible: false, data: null, x: 0, y: 0});
const showCreate = ref(false)
const showUpdate = ref(false)
const form = ref({
  id: '',
  street: '',
  town: null,
})
const freeLocations = ref([]);

const columns = [
  {key: "id", label: "ID"},
  {key: "street", label: "Street"},
  {key: "town", label: "Town"},
  {key: "operations", label: "Operations"}
];

let showTimer = null;
let hideTimer = null;
let eventSource = null; // для SSE

// ----------------- функции UI -----------------
function showDetails(data, event) {
  clearTimeout(hideTimer);
  showTimer = setTimeout(() => {
    popup.data = data;
    popup.x = event.pageX + 10;
    popup.y = event.pageY - 80;
    popup.visible = true;
  }, 100);
}

function hideDetails() {
  clearTimeout(showTimer);
  hideTimer = setTimeout(() => {
    popup.visible = false;
    popup.data = null;
  }, 100);
}

// ----------------- CRUD -----------------
async function addRow(row) {
  try {
    await axios.post('/addresses', row);
    showCreate.value = false;
  } catch (err) {
    console.error('Error saving addresses:', err);
  }
}

async function updateRow(row) {
  try {
    const payload = { ...row };
    delete payload.operations;
    delete payload.creationDate;

    await axios.patch('/addresses', payload);
    showUpdate.value = false;
  } catch(err) {
    console.error('Error updating addresses:', err);
  }
}

async function deleteRow(row) {
  try {
    await axios.delete(`/addresses/${row.id}`);
  } catch (err) {
    console.error('Error deleting addresses:', err);
  }
}

// ----------------- Modals -----------------
function openCreateModal() {
  resetForm();
  showCreate.value = true;
  fetchFreeLocations();
}

async function openUpdateModal(row) {
  form.value = { ...row }

  await fetchFreeLocations()
  if (row.town) freeLocations.value.push(row.town)

  showUpdate.value = true;
}

function resetForm() {
  form.value = {
    id: '',
    street: '',
    town: null,
  }
}

// ----------------- Fetch -----------------
async function fetchAddresses() {
  try {
    const res = await axios.get(`/addresses?page=${page.value}&size=10`);
    const addresses = res.data.data || [];

    addresses.forEach(row => {
      row.operations = [
        {type: "Edit", icon: markRaw(EditIcon), onClick: () => openUpdateModal(row)},
        {type: "Delete", icon: markRaw(DeleteIcon), onClick: () => deleteRow(row)}
      ];
    });

    rows.splice(0, rows.length, ...addresses);
    totalPages.value = res.data.totalPages ?? 1;

  } catch (err) {
    console.error("Error fetching addresses:", err);
  }
}

async function fetchFreeLocations() {
  try {
    const res = await axios.get('/addresses/free-locations');
    freeLocations.value = res.data || [];
  } catch (err) {
    console.error(err);
  }
}

// ----------------- SSE -----------------
onMounted(() => {
  fetchAddresses();
  // подписка на SSE
  const eventSource = new EventSource("http://localhost:8080/Lab_1-1.0-SNAPSHOT/api/events");

  eventSource.addEventListener("ADDRESS_CREATED", () => {
    fetchAddresses();
  });
  eventSource.addEventListener("ADDRESS_UPDATED", () => {
    fetchAddresses();
  });
  eventSource.addEventListener("ADDRESS_DELETED", () => {
    fetchAddresses();
  });
});

// закрываем SSE при размонтировании
onBeforeUnmount(() => {
  if(eventSource) {
    eventSource.close();
    eventSource = null;
  }
});

// пагинация
watch(page, fetchAddresses);
</script>

<template>
  <Header/>
  <div class="container">
    <div class="table-container">
      <div class="table-header">
        <h2>Addresses</h2>
        <Button label="Create" type="button" @click="openCreateModal"/>
      </div>
      <Table :columns="columns" :rows="rows">
        <template #cell-town="{ safeValue }">
          <div
            v-if="safeValue?.id"
            @mouseenter="safeValue && showDetails(safeValue, $event)"
            @mouseleave="hideDetails"
          >
            Town #{{ safeValue?.id ?? '' }}
          </div>
        </template>
      </Table>
    </div>
    <Pagination v-model:pageNumber="page" :totalPages="totalPages"/>
  </div>

  <!--  Всплывающее окно для отображения связанных объектов  -->
  <DetailsPopup
    class="details-popup"
    :data="popup.data"
    :x="popup.x"
    :y="popup.y"
    :visible="popup.visible"
  />

  <!--  Overlay  -->
  <div
    v-if="popup.visible || showCreate || showUpdate"
    class="overlay"
    @mouseover="hideDetails"
  ></div>

  <!--  Create Modal  -->
  <FormCreate
    v-if="showCreate"
    title="Create Address"
    :form="form"
    @submit="addRow"
    @cancel="showCreate = false"
  >

    <div class="form-group">
      <p>Street:</p>
      <Input v-model="form.street" placeholder="Street"/>
    </div>

    <div class="form-group">
      <p>Town:</p>
      <Select v-model="form.town" :options="freeLocations" labelKey="town" valueKey="id"/>
    </div>

  </FormCreate>


  <!--  Update Modal  -->
  <FormUpdate
    v-if="showUpdate"
    title="Update Organization"
    :form="form"
    @submit="updateRow"
    @cancel="showUpdate = false"
  >

    <div class="form-group">
      <p>Street:</p>
      <Input v-model="form.street" placeholder="Street"/>
    </div>

    <div class="form-group">
      <p>Town:</p>
      <Select v-model="form.town" :options="freeLocations" labelKey="town" valueKey="id"/>
    </div>

  </FormUpdate>

</template>


<style scoped>

h2, p {
  font-weight: lighter;
  margin: 0;
}

.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 1rem;
}

.table-container {
  padding: 0 1.5rem;
}

.table-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin: 2rem 1rem 1rem;
}

.details-popup {
  position: absolute;
  background: white;
  padding: 0.5rem 1rem;
  border: 1px solid black;
  border-radius: 0.5rem;
  font-size: 0.9rem;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.2);
  transition: opacity 0.2s;
  z-index: 500;
  pointer-events: none;
}

.form-group {
  align-items: center;
  display: flex;
  flex-direction: row;
  gap: 1rem;
}

</style>
