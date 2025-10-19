<script setup>
import Table from "@/components/table.vue";
import Header from "@/components/header.vue";
import Button from "@/components/button.vue";

import {markRaw, onBeforeUnmount, onMounted, reactive, ref, watch} from "vue";
import Pagination from "@/components/pagination.vue";
import axios from "@/axios.js";

import EditIcon from '@/assets/editIcon.svg?component'
import DeleteIcon from '@/assets/deleteIcon.svg?component'
import FormCreate from "@/components/forms/form-create.vue";
import Input from "@/components/input.vue";
import FormUpdate from "@/components/forms/form-update.vue";
import ErrorModal from "@/components/error-modal.vue";

// ----------------- состояние -----------------
const page = ref(1);
const totalPages = ref(1);
const rows = reactive([]);
const showCreate = ref(false)
const showUpdate = ref(false)
const form = ref({
  id: '',
  x: '',
  y: '',
  z: ''
})

const columns = [
  {key: "id", label: "ID"},
  {key: "x", label: "X"},
  {key: "y", label: "Y"},
  {key: "z", label: "Z"},
  {key: "operations", label: "Operations"}
];

let eventSource = null; // для SSE

const errorModal = ref(null);


// ----------------- CRUD -----------------
async function addRow(row) {
  try {
    await axios.post('/locations', row);
    showCreate.value = false;
  } catch (err) {
    errorModal.value.show(err.response?.data || 'Error saving location');
  }
}

async function updateRow(row) {
  try {
    const payload = {...row};
    delete payload.operations;

    await axios.patch('/locations', payload);
    showUpdate.value = false;
  } catch (err) {
    errorModal.value.show(err.response?.data || 'Error updating location');
  }
}

async function deleteRow(row) {
  try {
    await axios.delete(`/locations/${row.id}`);
  } catch (err) {
    errorModal.value.show(err.response?.data || 'Error deleting location');
  }
}

// ----------------- Modals -----------------
function openCreateModal() {
  resetForm();
  showCreate.value = true;
}

async function openUpdateModal(row) {
  form.value = {...row}
  showUpdate.value = true;
}

function resetForm() {
  form.value = {
    id: '',
    x: '',
    y: '',
    z: ''
  }
}

// ----------------- Fetch -----------------
async function fetchLocations() {
  try {
    const res = await axios.get(`/locations?page=${page.value}&size=8`);
    const locations = res.data.data || [];

    locations.forEach(row => {
      row.operations = [
        {type: "Edit", icon: markRaw(EditIcon), onClick: () => openUpdateModal(row)},
        {type: "Delete", icon: markRaw(DeleteIcon), onClick: () => deleteRow(row)}
      ];
    });

    rows.splice(0, rows.length, ...locations);
    totalPages.value = res.data.totalPages ?? 1;

  } catch (err) {
    errorModal.value.show(err.response?.data || 'Error fetching locations');
  }
}

// ----------------- SSE -----------------
onMounted(() => {
  fetchLocations();
  // подписка на SSE
  const eventSource = new EventSource("http://localhost:8080/Lab_1-1.0-SNAPSHOT/api/events");

  eventSource.addEventListener("LOCATION_CREATED", () => {
    fetchLocations();
  });
  eventSource.addEventListener("LOCATION_UPDATED", () => {
    fetchLocations();
  });
  eventSource.addEventListener("LOCATION_DELETED", () => {
    fetchLocations();
  });
});

// закрываем SSE при размонтировании
onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close();
    eventSource = null;
  }
});

// пагинация
watch(page, fetchLocations);
</script>

<template>
  <Header/>
  <div class="container">
    <div class="table-container">
      <div class="table-header">
        <h2>Locations</h2>
        <Button label="Create" type="button" @click="openCreateModal"/>
      </div>
      <Table :columns="columns"
             :rows="rows"
             :sortable-columns="[]"
      />
    </div>
    <Pagination v-model:pageNumber="page" :totalPages="totalPages"/>
  </div>

  <!--  Overlay  -->
  <div
    v-if="showCreate || showUpdate"
    class="overlay"
  ></div>

  <!--  Create Modal  -->
  <FormCreate
    v-if="showCreate"
    title="Create Location"
    :form="form"
    @submit="addRow"
    @cancel="showCreate = false"
  >

    <div class="form-group">
      <p>X:</p>
      <Input v-model="form.x" placeholder="X"/>
    </div>

    <div class="form-group">
      <p>Y:</p>
      <Input v-model="form.y" placeholder="Y"/>
    </div>

    <div class="form-group">
      <p>Z:</p>
      <Input v-model="form.z" placeholder="Z"/>
    </div>

  </FormCreate>


  <!--  Update Modal  -->
  <FormUpdate
    v-if="showUpdate"
    title="Update Location"
    :form="form"
    @submit="updateRow"
    @cancel="showUpdate = false"
  >

    <div class="form-group">
      <p>X:</p>
      <Input v-model="form.x" placeholder="X"/>
    </div>

    <div class="form-group">
      <p>Y:</p>
      <Input v-model="form.y" placeholder="Y"/>
    </div>

    <div class="form-group">
      <p>Z:</p>
      <Input v-model="form.z" placeholder="Z"/>
    </div>

  </FormUpdate>

  <!--  Error Modal  -->
  <ErrorModal ref="errorModal"/>

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
