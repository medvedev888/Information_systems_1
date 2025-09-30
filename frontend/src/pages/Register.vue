<script setup>
import Form from "@/components/form.vue";
import {reactive} from "vue";

const formData = reactive({login: "", password: "", confirmPassword: ""});

function checkPasswords() {
  console.log(formData.password, formData.confirmPassword)
  if (formData.password !== formData.confirmPassword) {
    alert("Passwords do not match!");
    return false;
  }
  return true;
}

function handleRegister(data) {
  Object.assign(formData, data);
  if (!checkPasswords()) return;
  // отправка на сервер
}
</script>


<template>
  <div class="container">
    <h1>Register Page</h1>
    <Form
      :fields="[
        { modelKey: 'login', text: 'Enter login', type: 'text' },
        { modelKey: 'password', text: 'Enter password', type: 'password' },
        { modelKey: 'confirmPassword', text: 'Repeat password', type: 'password' }
      ]"
      buttonLabel="Register"
      @submit="handleRegister"
    />
  </div>
</template>


<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  gap: 1rem;
}
</style>
