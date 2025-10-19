<script setup>
import {ref} from "vue";
import Button from "@/components/button.vue";

const visible = ref(false);
const message = ref("");

function show(msg) {
  message.value = msg;
  visible.value = true;
}

function close() {
  visible.value = false;
  message.value = "";
}

defineExpose({show});
</script>

<template>
  <div v-if="visible" class="modal-overlay" @click="close">
    <div class="modal" @click.stop>
      <h3>Error</h3>
      <p v-html="message.replace(/\n/g, '<br>')"></p>
      <Button label="Close" @click="close"/>
    </div>
  </div>
</template>

<style scoped>
h3 {
  margin-top: 0.5rem;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 1rem;
  border-radius: 0.5rem;
  min-width: 250px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border: 1px solid black;
}

button {
  margin-top: 2rem;
  padding: 0.5rem 1rem;
}
</style>
