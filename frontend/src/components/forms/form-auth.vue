<script setup>
import Input from "@/components/input.vue"
import Button from "@/components/button.vue"
import {reactive} from 'vue'


const props = defineProps({
  fields: {
    type: Array,
    default: () => []
  },
  buttonLabel: {
    type: String,
    default: 'Submit'
  }
})

const emit = defineEmits(['submit'])

const formData = reactive({})
props.fields.forEach(field => formData[field.modelKey] = '')


function handleSubmit(e) {
  e.preventDefault()
  emit('submit', {...formData})
}
</script>

<template>
  <form class="form" @submit="handleSubmit">
    <Input
      v-for="field in fields"
      :key="field.modelKey"
      v-model="formData[field.modelKey]"
      :text="field.text"
      :type="field.type || 'text'"
    />

    <Button
      :label="buttonLabel"
      type="submit"
    />
  </form>
</template>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 300px;
}
</style>
