<script setup>
import Button from "@/components/button.vue";

const props = defineProps({
  title: {type: String, default: 'Result'}
})

const emit = defineEmits(['close'])
</script>

<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="modal">
      <div class="modal-content">
        <h2>{{ title }}</h2>
        <div class="modal-body">
          <slot/>
        </div>
        <div class="actions">
          <Button @click="emit('close')" label="Close"/>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
h2 {
  margin-top: 0.7rem;
  margin-bottom: 0;
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
  padding: 0.5rem 2rem;
  border-radius: 0.5rem;
  min-width: 20rem;
  max-width: 90%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 1rem;
}

.modal-body {
  max-height: 60vh;
  overflow-y: auto;
}

.actions {
  margin-top: 1rem;
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  margin-bottom: 0.7rem;
}
</style>
