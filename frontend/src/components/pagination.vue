<script setup>
import Button from "@/components/button.vue";
import {ref, watch} from "vue";

const props = defineProps({
  pageNumber: {
    type: Number,
    default: 1
  },
  totalPages: {
    type: Number,
    default: 1
  }
});

const emit = defineEmits(["update:pageNumber"]);

const currentPage = ref(props.pageNumber);

watch(() => props.pageNumber, (newVal) => {
  currentPage.value = newVal;
});

function previousPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
    emit("update:pageNumber", currentPage.value);
  }
}

function nextPage() {
  if (currentPage.value < props.totalPages) {
    currentPage.value++;
    emit("update:pageNumber", currentPage.value);
  }
}
</script>


<template>
  <div class="pagination-container">
    <Button
      v-if="currentPage > 1"
      label="<"
      type="button"
      @click="previousPage"
    />
    <h4>{{ currentPage }}</h4>
    <Button
      v-if="currentPage < props.totalPages"
      label=">"
      type="button"
      @click="nextPage"
    />
  </div>
</template>


<style scoped>
.pagination-container {
  display: inline-flex;
  gap: 1rem;
  justify-content: center;
}

h4 {
  margin: 0.5rem;
  font-weight: lighter;
}

Button {
  padding: 0.25rem 0.8rem;
}
</style>
