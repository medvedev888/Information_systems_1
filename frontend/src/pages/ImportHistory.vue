<script setup>

import Table from "@/components/table.vue";
import Pagination from "@/components/pagination.vue";
import Header from "@/components/header.vue";
import {onBeforeUnmount, onMounted, reactive, ref, watch} from "vue";
import {showErrorFromResponse} from "@/utils/error.js";
import Input from "@/components/input.vue";
import Button from "@/components/button.vue";
import Select from "@/components/select.vue";

const page = ref(1);
const totalPages = ref(1);
const rows = reactive([]);
const columns = [
  {key: "id", label: "ID"},
  {key: "status", label: "Status"},
  {key: "user", label: "User"},
  {key: "count", label: "Count"}
];
let eventSource = null; // для SSE
const filterField = ref(null);
const filterValue = ref('');
const sortField = ref(null);
const sortOrder = ref(null);
const str_columns = [
  {key: "status", label: "Status"},
  {key: "user", label: "User"}
];
const errorModal = ref(null);
const fieldLabels = {
  id: "ID",
  status: "Status",
  user: "User",
  count: "Count"
};

// ----------------- Fetch -----------------
async function fetchImportHistory() {
  try {
    const params = new URLSearchParams({
      page: page.value,
      size: 8
    });

    if (filterField.value && filterValue.value) {
      params.append('filterField', filterField.value.key);
      params.append('filterValue', filterValue.value);
    }

    if (sortField.value && sortOrder.value) {
      params.append('sortField', sortField.value);
      params.append('sortOrder', sortOrder.value);
    }
  } catch (err) {
    showErrorFromResponse(err, errorModal, fieldLabels);
  }
}

// ----------------- SSE -----------------
onMounted(() => {
  fetchImportHistory();
  // подписка на SSE
  const eventSource = new EventSource("http://localhost:8080/Lab_1-1.0-SNAPSHOT/api/events");

  eventSource.addEventListener("ORGANIZATION_IMPORTED", () => {
    fetchImportHistory();
  });
});

// закрываем SSE при размонтировании
onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close();
    eventSource = null;
  }
});

// Сортировка
function handleSortChanged({field, order}) {
  sortField.value = field;
  sortOrder.value = order;
  fetchImportHistory();
}

// пагинация
watch(page, fetchImportHistory);

</script>

<template>
  <Header/>
  <div class="container">
    <div class="table-container">
      <div class="table-header">
        <h2>Import history</h2>
        <div class="filter-container">
          <Select
            v-model="filterField"
            :options="str_columns"
            valueKey="key"
            labelKey="label"
          />
          <Input v-model="filterValue" placeholder="Search string"></Input>
          <Button label="Confirm" @click="fetchAddresses"></Button>
        </div>
      </div>
      <Table :columns="columns"
             :rows="rows"
             :sort-field="sortField"
             :sort-order="sortOrder"
             @sortChanged="handleSortChanged"
             :sortable-columns="['status', 'user']"
      >
      </Table>
    </div>
    <Pagination v-model:pageNumber="page" :totalPages="totalPages"/>
  </div>

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
  margin: 2rem 1rem 1rem;
  gap: 25rem;
}

.filter-container {
  display: flex;
  gap: 1rem;
}

</style>
