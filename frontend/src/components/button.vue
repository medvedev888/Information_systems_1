<script setup>
defineProps({
  label: { type: String, default: "Click me" }
})
defineEmits(["click"])
</script>

<template>
  <button class="btn" @click="$emit('click')">
    {{ label }}
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
  transition: background 0.2s;
}

.btn:hover {
  background-color: #111827;
}
</style>

