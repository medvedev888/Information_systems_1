<script setup>

</script>

<template>
  <form class="form">
    <input type="text" class="input" placeholder="Name">
    <input type="email" class="input" placeholder="Email">
    <button class="btn" type="submit">Submit</button>
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
