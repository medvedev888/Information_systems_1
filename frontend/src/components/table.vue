<script setup>
import { defineEmits, defineProps } from "vue";
import Button from "@/components/button.vue";

const props = defineProps({
  columns: Array,
  rows: Array
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
    <tr v-for="(row, rowIndex) in rows" :key="rowIndex" @click="emit('rowClick', row)">
      <td v-for="col in columns" :key="col.key" class="td">
        <template v-if="col.key === 'operations' && row[col.key]">
          <div  class="button-container">
            <Button
              v-for="btn in row[col.key]"
              :key="btn.type"
              :label="btn.type"
              :icon="btn.icon"
              @click="btn.onClick"
            />
          </div>
        </template>
        <template v-else>
          {{ row[col.key] }}
        </template>
      </td>
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

td Button {
  padding: 0.7rem;
}

.button-container {
  display: flex;
  flex-direction: row;
  gap: 0.2rem;
}
</style>
