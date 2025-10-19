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
  y: ''
})

const columns = [
  {key: "id", label: "ID"},
  {key: "x", label: "X"},
  {key: "y", label: "Y"},
  {key: "operations", label: "Operations"}
];

let eventSource = null; // для SSE

const errorModal = ref(null);


// ----------------- CRUD -----------------
async function addRow(row) {
  try {
    await axios.post('/coordinates', row);
    showCreate.value = false;
  } catch (err) {
    errorModal.value.show(err.response?.data || 'Error saving coordinates');
  }
}

async function updateRow(row) {
  try {
    const payload = {...row};
    delete payload.operations;

    await axios.patch('/coordinates', payload);
    showUpdate.value = false;
  } catch (err) {
    errorModal.value.show(err.response?.data || 'Error updating coordinates');
  }
}

async function deleteRow(row) {
  try {
    await axios.delete(`/coordinates/${row.id}`);
  } catch (err) {
    errorModal.value.show(err.response?.data || 'Error deleting coordinates');
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
    y: ''
  }
}

// ----------------- Fetch -----------------
async function fetchCoordinates() {
  try {
    const res = await axios.get(`/coordinates?page=${page.value}&size=8`);
    const coordinates = res.data.data || [];

    coordinates.forEach(row => {
      row.operations = [
        {type: "Edit", icon: markRaw(EditIcon), onClick: () => openUpdateModal(row)},
        {type: "Delete", icon: markRaw(DeleteIcon), onClick: () => deleteRow(row)}
      ];
    });

    rows.splice(0, rows.length, ...coordinates);
    totalPages.value = res.data.totalPages ?? 1;

  } catch (err) {
    errorModal.value.show(err.response?.data || 'Error fetching coordinates');
  }
}

// ----------------- SSE -----------------
onMounted(() => {
  fetchCoordinates();
  // подписка на SSE
  const eventSource = new EventSource("http://localhost:8080/Lab_1-1.0-SNAPSHOT/api/events");

  eventSource.addEventListener("COORDINATES_CREATED", () => {
    fetchCoordinates();
  });
  eventSource.addEventListener("COORDINATES_UPDATED", () => {
    fetchCoordinates();
  });
  eventSource.addEventListener("COORDINATES_DELETED", () => {
    fetchCoordinates();
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
watch(page, fetchCoordinates);
</script>

<template>
  <Header/>
  <div class="container">
    <div class="table-container">
      <div class="table-header">
        <h2>Coordinates</h2>
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
    title="Create Coordinates"
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

  </FormCreate>


  <!--  Update Modal  -->
  <FormUpdate
    v-if="showUpdate"
    title="Update Coordinates"
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
