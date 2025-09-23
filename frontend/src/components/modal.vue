<!-- Modal.vue -->
<script setup>
import { defineEmits, defineProps } from "vue"

// Управление открытием + кастомные стили/размеры
const props = defineProps({
  modelValue: { type: Boolean, default: false },
  width: { type: String, default: "400px" },
  height: { type: String, default: "auto" },
  backgroundColor: { type: String, default: "white" },
  overlayColor: { type: String, default: "rgba(0,0,0,0.5)" }
})

const emit = defineEmits(["update:modelValue"])

function closeModal() {
  emit("update:modelValue", false)
}
</script>

<template>
  <div v-if="modelValue" :style="{ backgroundColor: overlayColor }" class="modal-overlay">
    <div class="modal-content" :style="{ width: width, height: height, backgroundColor: backgroundColor }">
      <slot></slot>
      <button class="btn" @click="closeModal">Close</button>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  padding: 1rem 2rem;
  border-radius: 0.5rem;
  min-width: 200px;
  max-width: 90%;
  box-sizing: border-box;
}

.btn {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background-color: black;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
}

.btn:hover {
  background-color: black;
}
</style>
