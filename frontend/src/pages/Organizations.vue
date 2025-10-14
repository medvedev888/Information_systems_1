<script setup>
import Table from "@/components/table.vue";
import Header from "@/components/header.vue";
import Button from "@/components/button.vue";

import {reactive, ref, onMounted, watch, markRaw} from "vue";
import Pagination from "@/components/pagination.vue";
import axios from "@/axios.js";

import EditIcon from '@/assets/editIcon.svg?component'
import DeleteIcon from '@/assets/deleteIcon.svg?component'
import DetailsPopup from "@/components/details-popup.vue";
import FormCreate from "@/components/forms/form-create.vue";
import Input from "@/components/input.vue";
import Select from "@/components/select.vue";

const page = ref(1);
const totalPages = ref(1);
const rows = reactive([]);
const popup = reactive({visible: false, data: null, x: 0, y: 0});
const showCreate = ref(false)
const form = ref({
  name: '',
  fullName: '',
  coordinates: null,
  officialAddress: null,
  annualTurnover: null,
  employeesCount: null,
  rating: null,
  type: null,
  postalAddress: null
})
const organizationTypes = ['COMMERCIAL', 'PUBLIC', 'TRUST'];



const columns = [
  {key: "id", label: "ID"},
  {key: "name", label: "Name"},
  {key: "coordinates", label: "Coordinates"},
  {key: "creationDate", label: "Creation Date"},
  {key: "officialAddress", label: "Official Address"},
  {key: "annualTurnover", label: "Annual Turnover"},
  {key: "employeesCount", label: "Employees Count"},
  {key: "rating", label: "Rating"},
  {key: "fullName", label: "Full Name"},
  {key: "type", label: "Type"},
  {key: "postalAddress", label: "Postal Address"},
  {key: "operations", label: "Operations"}
];

let showTimer = null;
let hideTimer = null;

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

function addRow(row) {

}

function editRow(row) {
  console.log("edit: " + row)
}

function deleteRow(row) {
  console.log("delete: " + row)
}

async function fetchOrganizations() {
  try {
    const res = await axios.get(`/organizations?page=${page.value}&size=1`);
    const orgs = res.data.data || [];

    orgs.forEach(row => {
      if (row.creationDate) {
        row.creationDate = new Date(row.creationDate).toLocaleString('ru-RU', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        });
      }

      row.operations = [
        {type: "Edit", icon: markRaw(EditIcon), onClick: () => editRow(row)},
        {type: "Delete", icon: markRaw(DeleteIcon), onClick: () => deleteRow(row)}
      ];
    });

    rows.splice(0, rows.length, ...orgs);
    totalPages.value = res.data.totalPages ?? 1;

    rows.forEach(row => {
      row.operations = [
        {type: "Edit", icon: markRaw(EditIcon), onClick: () => editRow(row)},
        {type: "Delete", icon: markRaw(DeleteIcon), onClick: () => deleteRow(row)}
      ];
    });


  } catch (err) {
    console.error("Error fetching organizations:", err);
  }
}

onMounted(fetchOrganizations);
watch(page, fetchOrganizations);
</script>

<template>
  <Header/>
  <div class="container">
    <div class="table-container">
      <div class="table-header">
        <h2>Organizations</h2>
        <Button label="Create" type="button" @click="showCreate = true"/>
      </div>
      <Table :columns="columns" :rows="rows">
        <template #cell-coordinates="{ row }">
          <div @mouseenter="showDetails(row.coordinates, $event)" @mouseleave="hideDetails">
            Coordinates #{{ row.coordinates.id }}
          </div>
        </template>

        <template #cell-officialAddress="{ row }">
          <div @mouseenter="showDetails(row.officialAddress, $event)" @mouseleave="hideDetails">
            Official Address #{{ row.officialAddress.id }}
          </div>
        </template>

        <template #cell-postalAddress="{ row }">
          <div @mouseenter="showDetails(row.postalAddress, $event)" @mouseleave="hideDetails">
            Postal Address #{{ row.postalAddress.id }}
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
    v-if="popup.visible || showCreate"
    class="overlay"
    @mouseover="hideDetails"
  ></div>

  <!--  Create Modal  -->
  <FormCreate
    v-if="showCreate"
    title="Create Organization"
    @submit="addRow"
    @cancel="showCreate = false"
  >
    <div class="form-group">
      <p>Name:</p>
      <Input v-model="form.name" placeholder="Name"/>
    </div>

    <div class="form-group">
      <p>Type:</p>
      <Select v-model="form.type" :options="organizationTypes" />
    </div>

    <div class="form-group">
      <p>Coordinates:</p>
      <Select v-model="form.coordinates" :options="coordinatesList" />
    </div>

    <div class="form-group">
      <p>Official Address:</p>
      <Select v-model="form.officialAddress" :options="addressesList" />
    </div>

    <div class="form-group">
      <p>Postal Address:</p>
      <Select v-model="form.postalAddress" :options="addressesList" />
    </div>

    <div class="form-group">
      <p>Annual turnover:</p>
      <Input v-model.number="form.annualTurnover" type="number" placeholder="Annual turnover"/>
    </div>

    <div class="form-group">
      <p>Employees count:</p>
      <Input v-model.number="form.employeesCount" type="number" placeholder="Employees count"/>
    </div>

    <div class="form-group">
      <p>Rating:</p>
      <Input v-model.number="form.rating" type="number" placeholder="Rating"/>
    </div>
  </FormCreate>

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
