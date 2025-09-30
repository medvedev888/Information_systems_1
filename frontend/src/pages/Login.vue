<script setup>
import Form from "@/components/form.vue"
import {reactive} from "vue";

const formData = reactive({login: "", password: ""});

function handleLogin(data) {
  Object.assign(formData, data);
  // отправка на сервер
}
</script>


<template>
  <div class="container">
    <h1>Login Page</h1>
    <Form
      :fields="[
        { modelKey: 'login', text: 'Enter login', type: 'text' },
        { modelKey: 'password', text: 'Enter password', type: 'password' }
      ]"
      buttonLabel="Login"
      @submit="handleLogin"
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
