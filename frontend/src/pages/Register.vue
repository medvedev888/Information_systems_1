<script setup>
import Form from "@/components/form.vue";
import Link from "@/components/link.vue";
import {reactive} from "vue";
import router from "@/router/index.js";

const formData = reactive({login: "", password: "", confirmPassword: ""});

function checkPasswords() {
  if (formData.password !== formData.confirmPassword) {
    alert("Passwords do not match!");
    return false;
  }
  return true;
}

async function authRequest(endpoint, login, password) {
  try {
    const res = await fetch(`http://localhost:8080/Lab_1-1.0-SNAPSHOT/api/auth/${endpoint}`, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({login, password})
    });
    const data = await res.json();

    if (res.ok) {
      localStorage.setItem("jwt", data.token);
      return {success: true};
    } else {
      return {success: false, errors: data.errors || {general: data.message}};
    }
  } catch (err) {
    return {success: false, errors: {general: "Network error"}};
  }
}

async function handleRegister(data) {
  Object.assign(formData, data);
  if (!checkPasswords()) return;

  const result = await authRequest("register", formData.login, formData.password);

  if (result.success) {
    await router.push("/");
  } else {
    alert(Object.values(result.errors).join("\n"));
  }
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
    <div class="container2">
      <h4>If you already have an account:</h4>
      <Link
        label="Login"
        href="/login">
      </Link>
    </div>
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

.container2 {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 1rem;
}

h1, h4 {
  margin: 0;
  font-weight: 300;
}
</style>
