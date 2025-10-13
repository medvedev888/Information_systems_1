<script setup>
defineProps({
  label: {type: String, default: "Click me"},
  type: {type: String, default: "button"},
  icon: Object
})
defineEmits(["click"])
</script>

<template>
  <button :type="type" class="btn" @click="$emit('click')">
    <component v-if="icon" :is="icon" class="btn-icon"/>
    <span v-else-if="label">{{ label }}</span>
  </button>
</template>

<style>
.btn {
  padding: 0.5rem 1.2rem;
  background-color: black;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn:hover {
  background-color: #111827;
}

.btn-icon {
  width: 16px;
  height: 16px;
  fill: currentColor;
}
</style>
