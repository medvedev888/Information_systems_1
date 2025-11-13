<script setup>
import Button from "@/components/button.vue"
import {ref, watch} from "vue"

const props = defineProps({
  title: {type: String, default: "Import"},
  file: {type: File, default: null}
})

const emit = defineEmits(['update:file', 'submit', 'cancel'])

const fileInput = ref(null)
const fileName = ref(props.file ? props.file.name : null)

watch(() => props.file, (newFile) => {
  fileName.value = newFile ? newFile.name : null
})

function triggerFile() {
  fileInput.value.click()
}

function handleFileChange(event) {
  const selected = event.target.files[0]
  fileName.value = selected ? selected.name : null
  emit('update:file', selected)
}

function submitImport() {
  if (!props.file) {
    alert("Select file")
    return
  }
  emit('submit', props.file)
}
</script>

<template>
  <div class="modal-overlay" @click.self="emit('cancel')">
    <div class="modal">
      <div class="modal-content">
        <h2>{{ title }}</h2>
        <div class="modal-content-main">
          <input
            ref="fileInput"
            type="file"
            accept=".json,.csv,.xlsx"
            style="display: none"
            @change="handleFileChange"
          />
          <Button class="select-button"
                  @click="triggerFile"
                  label="Select file"
          ></Button>
          <span v-if="fileName">{{ fileName }}</span>
        </div>

        <div class="actions">
          <Button label="Import" @click="submitImport"/>
          <Button label="Cancel" @click="emit('cancel')"/>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>
h2 {
  margin-top: 0.7rem;
  margin-bottom: 0.7rem;
}

.modal {
  background-color: white;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
  border: 1px solid black;
  border-radius: 0.5rem;
  align-items: center;
  display: flex;
  justify-content: center;
}

.modal-content {
  padding: 1rem 2rem;
  border-radius: 0.5rem;
  min-width: 15rem;
  max-width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 1rem;
}

.actions {
  margin-top: 0.5rem;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-bottom: 0.4rem;
}

.modal-content-main {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  gap: 1.5rem;
}

.select-button {
  width: 7rem;
  justify-content: center;
  margin: 0.8rem 0;
}

</style>
