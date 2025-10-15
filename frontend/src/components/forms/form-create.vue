<script setup>
import Button from "@/components/button.vue";

const props = defineProps({
  title: {type: String, default: 'Create'},
  form: { type: Object, required: true }
})

const emit = defineEmits(['submit', 'cancel'])
</script>

<template>
  <div class="modal-overlay" @click.self="emit('cancel')">
    <div class="modal">

      <div class="modal-content">
        <h2>{{ title }}</h2>
        <slot/>
        <div class="actions">
          <Button @click="emit('submit', props.form)"
                  label="Create"
          />
          <Button @click="emit('cancel')"
                  label="Cancel"
          />
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
  margin-top: 1rem;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-bottom: 0.7rem;
}
</style>
