<script setup>
import {defineProps, defineEmits} from "vue";

const props = defineProps({
  columns: Array, // [{ key: 'login', label: 'Login' }, ...]
  rows: Array     // [{ login: 'Vlad', email: '...' }, ...]
});
const emit = defineEmits(["rowClick"]);
</script>

<template>
  <table class="table">
    <thead>
    <tr class="header-row">
      <th v-for="col in columns" :key="col.key">{{ col.label }}</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="(row, index) in rows" :key="index" @click="emit('rowClick', row)">
      <td v-for="col in columns" :key="col.key">{{ row[col.key] }}</td>
    </tr>
    </tbody>
  </table>
</template>

<style scoped>
.table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  border-radius: 1rem;
  overflow: hidden;
  border: 1px solid black;
}

th, td {
  padding: 0.5rem 1rem;
  text-align: center;
}

tr:hover {
  background: rgba(79, 70, 229, 0.3);
  cursor: pointer;
}

.header-row th {
  border-collapse: collapse;
  border-bottom: 1px solid black;
}
</style>
