<script setup>
import {onBeforeUnmount, onMounted, reactive, ref} from "vue";
import axios from "@/axios.js";
import {showErrorFromResponse} from "@/utils/error.js";

import Header from "@/components/header.vue";
import Button from "@/components/button.vue";
import Input from "@/components/input.vue";
import Select from "@/components/select.vue";
import ResultModal from "@/components/result-modal.vue";
import Table from "@/components/table.vue";
import DetailsPopup from "@/components/details-popup.vue";
import ErrorModal from "@/components/error-modal.vue";

// ----------------- состояние -----------------
const organizationTypes = ['COMMERCIAL', 'GOVERNMENT', 'TRUST'];

const ratingValue = ref('');
const selectedType = ref(null);

const mergeOrg1 = ref(null);
const mergeOrg2 = ref(null);
const mergeName = ref('');
const mergeAddress = ref(null);

const absorbAcquirerOrganization = ref(null);
const absorbVictimOrganization = ref(null);

const organizations = ref([]);
const freeOfficialAddresses = ref([]);

const errorModal = ref(null);
const fieldLabels = {
  id: "ID",
  name: "Name",
  coordinates: "Coordinates",
  creationDate: "Creation Date",
  officialAddress: "Official Address",
  annualTurnover: "Annual Turnover",
  employeesCount: "Employees Count",
  rating: "Rating",
  fullName: "Full Name",
  type: "Type",
  postalAddress: "Postal Address"
};

let eventSource = null; // для SSE

const operation1Result = ref(null);
const operation2Result = ref([]);
const operation3Result = ref([]);
const operation4Result = ref([]);

const showModal = ref(false);
const completedOperation = ref(0);

const columns = [
  {key: "id", label: "ID"},
  {key: "name", label: "Name"},
  {key: "coordinates", label: "Coordinates"},
  {key: "creationDate", label: "Creation Date"},
  {key: "officialAddress", label: "Official Address"},
  {key: "annualTurnover", label: "Annual Turnover"},
  {key: "employeesCount", label: "Employees Count"},
  {key: "rating", label: "Rating"},
  {key: "fullName", label: "Full Name"},
  {key: "type", label: "Type"},
  {key: "postalAddress", label: "Postal Address"}
];

let showTimer = null;
let hideTimer = null;
const popup = reactive({visible: false, data: null, x: 0, y: 0});


// ----------------- функции UI -----------------
function showDetails(data, event) {
  clearTimeout(hideTimer);
  showTimer = setTimeout(() => {
    popup.data = data;
    popup.x = event.pageX + 10;
    popup.y = event.pageY - 80;
    popup.visible = true;
  }, 100);
}

function hideDetails() {
  clearTimeout(showTimer);
  hideTimer = setTimeout(() => {
    popup.visible = false;
    popup.data = null;
  }, 100);
}


// ----------------- Fetch -----------------
async function fetchAllOrganizations() {
  try {
    const res = await axios.get(`/organizations?page=1&size=50`);
    organizations.value = res.data.data || [];
  } catch (err) {
    showErrorFromResponse(err, errorModal, fieldLabels);
  }
}

async function fetchFreeAddresses() {
  try {
    const res = await axios.get(`/organizations/free-addresses?type=official`);
    freeOfficialAddresses.value = res.data || [];
  } catch (err) {
    showErrorFromResponse(err, errorModal, fieldLabels);
  }
}

// ----------------- Валидация -----------------
function validateSelectedOrganizations(org1, org2) {
  if (!org1 || !org2) throw new Error("Выберите обе организации");
  if (org1.id === org2.id) throw new Error("Нельзя выбрать одну и ту же организацию");
}


// ----------------- Операции -----------------
async function handleOperation1() {
  try {
    const res = await axios.get(`/organizations/operations/count-by-rating`, {
      params: {rating: ratingValue.value}
    });
    operation1Result.value = res.data;
    console.log(operation1Result.value);
    completedOperation.value = 1;
    showModal.value = true;
  } catch (err) {
    showErrorFromResponse(err, errorModal, fieldLabels);
  }
}

async function handleOperation2() {
  try {
    const res = await axios.get(`/organizations/operations/by-type`, {
      params: {type: selectedType.value}
    });
    operation2Result.value = res.data || [];
    completedOperation.value = 2;
    showModal.value = true;
  } catch (err) {
    showErrorFromResponse(err, errorModal, fieldLabels);
  }
}

async function handleOperation3() {
  try {
    const res = await axios.get(`/organizations/operations/unique-fullnames`);
    operation3Result.value = res.data || [];
    completedOperation.value = 3;
    showModal.value = true;
  } catch (err) {
    showErrorFromResponse(err, errorModal, fieldLabels);
  }
}

async function handleOperation4() {
  try {
    validateSelectedOrganizations(mergeOrg1.value, mergeOrg2.value);

    const payload = {
      organization1: mergeOrg1.value,
      organization2: mergeOrg2.value,
      name: mergeName.value,
      officialAddress: mergeAddress.value
    };

    const res = await axios.post(`/organizations/operations/merge`, payload);

    mergeOrg1.value = null;
    mergeOrg2.value = null;
    mergeName.value = '';
    mergeAddress.value = '';

    operation4Result.value = Array.isArray(res.data) ? res.data : [res.data];
    completedOperation.value = 4;
    showModal.value = true;
  } catch (err) {
    showErrorFromResponse(err, errorModal, fieldLabels);
  }
}

async function handleOperation5() {
  try {
    validateSelectedOrganizations(absorbAcquirerOrganization.value, absorbVictimOrganization.value);

    const payload = {
      acquirerOrganization: absorbAcquirerOrganization.value,
      victimOrganization: absorbVictimOrganization.value
    };

    await axios.post(`/organizations/operations/absorb`, payload);

    absorbAcquirerOrganization.value = null;
    absorbVictimOrganization.value = null;
    completedOperation.value = 5;
    showModal.value = true;
  } catch (err) {
    showErrorFromResponse(err, errorModal, fieldLabels);
  }
}


// ----------------- SSE -----------------
onMounted(() => {
  fetchAllOrganizations();
  fetchFreeAddresses();

  eventSource = new EventSource("http://localhost:8080/Lab_1-1.0-SNAPSHOT/api/events");

  const refreshOrganization = () => fetchAllOrganizations();
  const refreshAddresses = () => fetchFreeAddresses();

  ["ORGANIZATION_CREATED", "ORGANIZATION_UPDATED", "ORGANIZATION_DELETED", "ORGANIZATION_MERGED", "ORGANIZATION_ABSORBED"]
    .forEach(event => eventSource.addEventListener(event, () => {
      refreshOrganization();
      refreshAddresses();
    }));

  ["ADDRESS_CREATED", "ADDRESS_UPDATED", "ADDRESS_DELETED"]
    .forEach(event => eventSource.addEventListener(event, refreshAddresses));

});


onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close();
    eventSource = null;
  }
});
</script>


<template>
  <Header/>
  <div class="container">
    <div class="operations-container">

      <div class="first-operation">
        <h3>Получить количество объектов, значение поля rating которых меньше заданного</h3>
        <form class="form" @submit.prevent="handleOperation1">
          <Input v-model="ratingValue" type="text" placeholder="Enter rating"/>
          <Button label="Confirm" type="submit"/>
        </form>
      </div>

      <div class="second-operation">
        <h3>Получить массив объектов, значение поля type которых равно заданному</h3>
        <form class="form" @submit.prevent="handleOperation2">
          <Select v-model="selectedType" :options="organizationTypes"/>
          <Button label="Confirm" type="submit"/>
        </form>
      </div>

      <div class="third-operation">
        <h3>Получить массив уникальных значений поля fullName по всем объектам</h3>
        <form class="form" @submit.prevent="handleOperation3">
          <Button label="Confirm" type="submit"/>
        </form>
      </div>

      <div class="fourth-operation">
        <h3>Объединить организации, создав новую и зачислив в неё всех сотрудников двух
          исходных</h3>
        <form class="form" @submit.prevent="handleOperation4">
          <Select v-model="mergeOrg1" :options="organizations" valueKey="id" labelKey="name"/>
          <Select v-model="mergeOrg2" :options="organizations" valueKey="id" labelKey="name"/>
          <Input v-model="mergeName" type="text" placeholder="Enter name"/>
          <Select v-model="mergeAddress" :options="freeOfficialAddresses" valueKey="id"
                  labelKey="street"/>
          <Button label="Confirm" type="submit"/>
        </form>
      </div>

      <div class="fifth-operation">
        <h3>Поглощение одной организацией другой без увольнения сотрудников</h3>
        <form class="form" @submit.prevent="handleOperation5">
          <div class="org-1">
            <p>Поглощающая организация:</p>
            <Select v-model="absorbAcquirerOrganization" :options="organizations" valueKey="id"
                    labelKey="name"/>
          </div>
          <div class="org-2">
            <p>Поглощаемая организация:</p>
            <Select v-model="absorbVictimOrganization" :options="organizations" valueKey="id"
                    labelKey="name"/>
          </div>
          <Button label="Confirm" type="submit"/>
        </form>
      </div>

    </div>
  </div>


  <ResultModal
    v-if="showModal"
    :title="'Operation ' + completedOperation + ' result:'"
    @close="showModal = false"
  >
    <template v-if="completedOperation === 1">
      <h3>{{ operation1Result.count }}</h3>
    </template>

    <template v-if="completedOperation === 2">
      <Table :columns="columns"
             :rows="operation2Result"
             :sortable-columns="[]"
      >
        <template #cell-coordinates="{ safeValue }">
          <div
            v-if="safeValue?.id"
            @mouseenter="safeValue && showDetails(safeValue, $event)"
            @mouseleave="hideDetails"
          >
            Coordinates #{{ safeValue?.id ?? '' }}
          </div>
        </template>

        <template #cell-officialAddress="{ safeValue }">
          <div
            v-if="safeValue?.id"
            @mouseenter="safeValue && showDetails(safeValue, $event)"
            @mouseleave="hideDetails"
          >
            Official Address #{{ safeValue?.id ?? '' }}
          </div>
        </template>

        <template #cell-postalAddress="{ safeValue }">
          <div
            v-if="safeValue?.id"
            @mouseenter="safeValue && showDetails(safeValue, $event)"
            @mouseleave="hideDetails"
          >
            Postal Address #{{ safeValue?.id ?? '' }}
          </div>
        </template>
      </Table>
    </template>

    <template v-if="completedOperation === 3" v-for="x in operation3Result">
      <h4>{{ x }}</h4>
    </template>

    <template v-if="completedOperation === 4">
      <Table :columns="columns"
             :rows="operation4Result"
             :sortable-columns="[]"
      >
        <template #cell-coordinates="{ safeValue }">
          <div
            v-if="safeValue?.id"
            @mouseenter="safeValue && showDetails(safeValue, $event)"
            @mouseleave="hideDetails"
          >
            Coordinates #{{ safeValue?.id ?? '' }}
          </div>
        </template>

        <template #cell-officialAddress="{ safeValue }">
          <div
            v-if="safeValue?.id"
            @mouseenter="safeValue && showDetails(safeValue, $event)"
            @mouseleave="hideDetails"
          >
            Official Address #{{ safeValue?.id ?? '' }}
          </div>
        </template>

        <template #cell-postalAddress="{ safeValue }">
          <div
            v-if="safeValue?.id"
            @mouseenter="safeValue && showDetails(safeValue, $event)"
            @mouseleave="hideDetails"
          >
            Postal Address #{{ safeValue?.id ?? '' }}
          </div>
        </template>
      </Table>
    </template>

    <template v-if="completedOperation === 5">
      <h3>Поглощение прошло успешно</h3>
    </template>
  </ResultModal>


  <!--  Всплывающее окно для отображения связанных объектов  -->
  <DetailsPopup
    class="details-popup"
    :data="popup.data"
    :x="popup.x"
    :y="popup.y"
    :visible="popup.visible"
  />

  <!--  Overlay  -->
  <div
    v-if="popup.visible || showModal"
    class="overlay"
    @mouseover="hideDetails"
  ></div>


  <ErrorModal ref="errorModal"/>

</template>


<style scoped>
h2, h3, h4, p {
  font-weight: lighter;
  margin: 0;
}

.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.operations-container {
  padding: 1.5rem 5rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 2rem;
}

.first-operation,
.second-operation,
.third-operation,
.fourth-operation,
.fifth-operation {
  padding: 0.5rem 0;
}

.first-operation form,
.second-operation form,
.third-operation form,
.fourth-operation form,
.fifth-operation form {
  padding-top: 0.5rem;
  display: flex;
}

.fifth-operation form .org-1,
.fifth-operation form .org-2 {
  padding-top: 0.5rem;
  flex-direction: column;
}

.fifth-operation form Button {
  height: 2rem;
  align-self: end;
}


.first-operation form Input,
.second-operation form Select,
.fifth-operation form Select,
.fifth-operation form Input {
  width: 20rem;
  margin-right: 0.5rem;
}

.fourth-operation form Select,
.fourth-operation form Input {
  width: 15rem;
  margin-right: 0.5rem;
}

.details-popup {
  position: absolute;
  background: white;
  padding: 0.5rem 1rem;
  border: 1px solid black;
  border-radius: 0.5rem;
  font-size: 0.9rem;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.2);
  transition: opacity 0.2s;
  z-index: 500;
  pointer-events: none;
}

</style>
