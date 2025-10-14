<script setup>
import {defineProps, defineEmits} from 'vue'

const props = defineProps({
  modelValue: null,              // выбранное значение
  options: {type: Array, default: () => []}, // список вариантов
  labelKey: {type: String, default: 'label'},// что отображаем
  valueKey: {type: String, default: 'value'},// что сохраняем
})

const emit = defineEmits(['update:modelValue'])

function onChange(event) {
  emit('update:modelValue', event.target.value)
}
</script>

<template>
  <select :value="modelValue" @change="onChange" class="select">
    <option disabled value="">-- Select --</option>

    <option
      v-for="(opt, i) in options"
      :key="i"
      :value="typeof opt === 'object' ? opt[valueKey] : opt"
    >
      {{ typeof opt === 'object' ? opt[labelKey] : opt }}
    </option>
  </select>
</template>

<style scoped>
.select {
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #ccc;
  width: 100%;
  box-sizing: border-box;
  height: 2rem;
}

.select:focus {
  border-color: black;
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.3);
}
</style>
