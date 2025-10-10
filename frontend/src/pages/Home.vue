<script setup>
import Table from "@/components/table.vue";
import Header from "@/components/header.vue";

import {reactive, ref} from "vue";
import Pagination from "@/components/pagination.vue";

const page = ref(1);

// Определяем колонки
const columns = [
  { key: "id", label: "ID" },
  { key: "name", label: "Name" },
  { key: "coordinates", label: "Coordinates" },
  { key: "creationDate", label: "Creation Date" },
  { key: "officialAddress", label: "Official Address" },
  { key: "annualTurnover", label: "Annual Turnover" },
  { key: "employeesCount", label: "Employees Count" },
  { key: "rating", label: "Rating" },
  { key: "fullName", label: "Full Name" },
  { key: "type", label: "Type" },
  { key: "postalAddress", label: "Postal Address" }
];

// Данные для таблицы (стринги вместо объектов)
const rows = reactive([
  {
    id: 1,
    name: "Org A",
    coordinates: "X: 10, Y: 20",
    creationDate: "2025-10-10",
    officialAddress: "Street 1, City A",
    annualTurnover: "1000000",
    employeesCount: 50,
    rating: 4.5,
    fullName: "Organization Alpha",
    type: "COMMERCIAL",
    postalAddress: "Postal Street 1"
  },
  {
    id: 2,
    name: "Org B",
    coordinates: "X: 5, Y: 15",
    creationDate: "2023-05-01",
    officialAddress: "Street 2, City B",
    annualTurnover: "500000",
    employeesCount: 30,
    rating: 3.8,
    fullName: "Organization Beta",
    type: "GOVERNMENT",
    postalAddress: "Postal Street 2"
  }
]);


function handleRowClick(row) {
  alert(`Clicked on: ${row.login}`);
}
</script>


<template>
  <Header/>
  <div class="container">
    <div class="table-container">
      <h2>Organizations</h2>
      <Table :columns="columns" :rows="rows" @rowClick="handleRowClick"/>
    </div>
      <Pagination
        v-model:pageNumber="page"
        :totalPages="3"
      />
  </div>
</template>


<style scoped>
h2 {
  font-weight: lighter;
  margin-bottom: 0.7rem;
  margin-top: 1.4rem;
  margin-left: 1rem;
}

.table-container {
  padding: 0 2rem;
}

.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 1rem;
}
</style>
