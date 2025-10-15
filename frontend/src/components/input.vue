<script setup>
const props = defineProps({
  text: {type: String, default: "Enter text"},
  modelValue: {type: String, default: ""},
  type: {type: String, default: "text"}
})

const emit = defineEmits(["update:modelValue"])
</script>

<template>
  <input
    :type="props.type"
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
  height: 2rem;
}

.input:focus {
  border-color: black;
  outline: none;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.3);
}
</style>
