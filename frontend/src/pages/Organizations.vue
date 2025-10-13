<script setup>
import Table from "@/components/table.vue";
import Header from "@/components/header.vue";
import Button from "@/components/button.vue";

import {reactive, ref, onMounted, watch, markRaw} from "vue";
import Pagination from "@/components/pagination.vue";
import axios from "@/axios.js";

import EditIcon from '@/assets/editIcon.svg?component'
import DeleteIcon from '@/assets/deleteIcon.svg?component'

const page = ref(1);
const totalPages = ref(1);
const rows = reactive([]);

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

function addRow(row) {
  console.log("add: " + row)
}

function editRow(row) {
  console.log("edit: " + row)
}

function deleteRow(row) {
  console.log("delete: " + row)
}

async function fetchOrganizations() {
  try {
    const res = await axios.get(`/organizations?page=${page.value}&size=3`);
    rows.splice(0, rows.length, ...res.data.data || []);
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
        <Button label="Create" type="button" @click="addRow"/>
      </div>
      <Table :columns="columns" :rows="rows"/>
    </div>
    <Pagination v-model:pageNumber="page" :totalPages="totalPages"/>
  </div>
</template>

<style scoped>
h2 {
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

</style>
