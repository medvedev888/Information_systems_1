<script setup>
import {defineEmits, defineProps} from "vue";
import Button from "@/components/button.vue";

const props = defineProps({
  columns: Array,
  rows: Array
});

const emit = defineEmits(["rowClick"]);


function formatCellValue(value) {
  if (value === null || value === undefined) {
    return {display: '', safe: null};
  }

  const t = typeof value;
  if (t === 'string' || t === 'number' || t === 'boolean') {
    return {display: String(value), safe: value};
  }

  if (typeof value === 'object') {

    if ('id' in value && (value.id !== null && value.id !== undefined)) {
      return {display: String(value.id), safe: value};
    }

    const keys = Object.keys(value);
    for (const k of keys) {
      const v = value[k];
      if (v === null || v === undefined) continue;
      const vt = typeof v;
      if (vt === 'string' || vt === 'number' || vt === 'boolean') {
        return {display: `${k}: ${String(v)}`, safe: value};
      }
    }
    try {
      const s = JSON.stringify(value);
      return {display: s.length > 60 ? s.slice(0, 57) + '...' : s, safe: value};
    } catch (e) {
      return {display: '[object]', safe: value};
    }
  }

  return {display: String(value), safe: value};
}
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
          <div class="button-container">
            <Button
              v-for="btn in row[col.key]"
              :key="btn.type"
              :label="btn.type"
              :icon="btn.icon"
              @click.stop="btn.onClick"
            />
          </div>
        </template>

        <template v-else>
          <template v-if="true">
            <slot
              :name="`cell-${col.key}`"
              :row="row"
              :value="row[col.key]"
              :safeValue="formatCellValue(row[col.key]).safe"
            >
              {{ formatCellValue(row[col.key]).display }}
            </slot>
          </template>
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

td:hover {
  background: rgba(79, 70, 229, 0.3);
  cursor: pointer;
}

.header-row th {
  border-collapse: collapse;
  border-bottom: 1px solid black;
}

td Button {
  padding: 0.6rem;
}

.button-container {
  display: flex;
  flex-direction: row;
  gap: 0.2rem;
}
</style>
