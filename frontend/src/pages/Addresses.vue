<script setup>
import Table from "@/components/table.vue";
import Header from "@/components/header.vue";

import {reactive, ref, onMounted, watch} from "vue";
import Pagination from "@/components/pagination.vue";
import axios from "@/axios.js";

const page = ref(1);
const totalPages = ref(1);
const rows = reactive([]);

const columns = [
  { key: "id", label: "ID" },
  { key: "street", label: "Street" },
  { key: "town", label: "Town" },
  { key: "operation", label: "Operations"}
];


function handleRowClick(row) {
  alert(`Clicked on: ${row.login}`);
}

async function fetchOrganizations() {
  try {
    const res = await axios.get(`/addresses?page=${page.value}&size=3`);
    rows.splice(0, rows.length, ...res.data.data);
    totalPages.value = res.data.totalPages;
  } catch (err) {
    if (err.response) {
      console.error("Error fetching organizations:", err.response.data.message || "Unknown error");
    } else {
      console.error("Network error:", err);
    }
  }
}

onMounted(fetchOrganizations);
watch(page, fetchOrganizations);
</script>


<template>
  <Header/>
  <div class="container">
    <div class="table-container">
      <h2>Addresses</h2>
      <Table :columns="columns" :rows="rows" @rowClick="handleRowClick"/>
    </div>
    <Pagination
      v-model:pageNumber="page"
      :totalPages="totalPages"
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
  padding: 0 1.5rem;
}

.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 1rem;
}
</style>
