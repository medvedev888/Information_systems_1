<script setup>
const props = defineProps({
  text: { type: String, default: "Enter text" },
  modelValue: { type: String, default: "" }
})

const emit = defineEmits(["update:modelValue"])
</script>

<template>
  <input
    type="text"
    class="input"
    :placeholder="text"
    :value="props.modelValue"
    @input="emit('update:modelValue', $event.target.value)"
  />
</template>

<style scoped>
.input {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  width: 100%;
  box-sizing: border-box;
}

.input:focus {
  border-color: black;
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.3);
}
</style>
