<script setup>


import Table from "@/components/table.vue";
import Pagination from "@/components/pagination.vue";
import Header from "@/components/header.vue";
import {markRaw, onBeforeUnmount, onMounted, reactive, ref, watch} from "vue";
import {showErrorFromResponse} from "@/utils/error.js";
import Input from "@/components/input.vue";
import Button from "@/components/button.vue";
import Select from "@/components/select.vue";
import axios from "@/axios.js";
import AddAdminIcon from "@/assets/addAdmin.svg";

const page = ref(1);
const totalPages = ref(1);
const rows = reactive([]);
const columns = [
  {key: "id", label: "ID"},
  {key: "login", label: "User"},
  {key: "role", label: "Role"},
  {key: "operations", label: "Operations"}
];
let eventSource = null; // для SSE
const filterField = ref(null);
const filterValue = ref('');
const sortField = ref(null);
const sortOrder = ref(null);
const str_columns = [
  {key: "login", label: "User"},
  {key: "role", label: "Role"}
];
const errorModal = ref(null);
const fieldLabels = {
  id: "ID",
  login: "User",
  role: "Role"
};

// ----------------- CRUD -----------------
async function promoteToAdmin(row) {
  try {
    const payload = {
      id: row.id,
      login: row.login,
      role: "ADMIN"
    };
    delete payload.operations;
    console.log(payload)
    await axios.patch('/auth/roles', payload);
  } catch (err) {
    showErrorFromResponse(err, errorModal, fieldLabels);
  }
}

// ----------------- Fetch -----------------
async function fetchUsers() {
  try {
    const params = new URLSearchParams({
      page: page.value,
      size: 10
    });

    if (filterField.value && filterValue.value) {
      params.append('filterField', filterField.value.key);
      params.append('filterValue', filterValue.value);
    }

    if (sortField.value && sortOrder.value) {
      params.append('sortField', sortField.value);
      params.append('sortOrder', sortOrder.value);
    }
    const res = await axios.get(`/auth/users?${params.toString()}`);
    const users = res.data.data || [];

    users.forEach(row => {
      if(row.role!=="ADMIN") {
        row.operations = [
          {type: "Edit", icon: markRaw(AddAdminIcon), onClick: () => promoteToAdmin(row)},
        ];
      }
    });

    rows.splice(0, rows.length, ...users);
    totalPages.value = res.data.totalPages ?? 1;
  } catch (err) {
    showErrorFromResponse(err, errorModal, fieldLabels);
  }
}

// ----------------- SSE -----------------
onMounted(() => {
  fetchUsers();
  // подписка на SSE
  const eventSource = new EventSource("http://localhost:8080/Lab_1-1.0-SNAPSHOT/api/events");

  eventSource.addEventListener("USER_CREATED", () => {
    fetchUsers();
  });

  eventSource.addEventListener("USER_UPDATED", () => {
    fetchUsers();
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
  fetchUsers();
}

// пагинация
watch(page, fetchUsers);
</script>


<template>
  <Header/>
  <div class="container">
    <div class="table-container">
      <div class="table-header">
        <h2>Admin Panel</h2>
        <div class="filter-container">
          <Select
            v-model="filterField"
            :options="str_columns"
            valueKey="key"
            labelKey="label"
          />
          <Input v-model="filterValue" placeholder="Search string"></Input>
          <Button label="Confirm" @click="fetchUsers"></Button>
        </div>
      </div>
      <Table :columns="columns"
             :rows="rows"
             :sort-field="sortField"
             :sort-order="sortOrder"
             @sortChanged="handleSortChanged"
             :sortable-columns="['login', 'role']"
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
