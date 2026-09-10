<script setup>
import { ref, computed  } from "vue";

const emit = defineEmits(["add-task"]);

const title = ref("");
const description = ref("");

const isFormValid = computed(() => {
  return title.value.trim().length > 0;
});

const handleSubmit = () => {
  const trimmedTitle = title.value.trim();

  if (!trimmedTitle) {
    return;
  }

  emit("add-task", {
    title: trimmedTitle,
    description: description.value.trim(),
  });

  title.value = "";
  description.value = "";
};
</script>

<template>
  <div class="mb-8 rounded-xl bg-white p-6 shadow">
    <h2 class="mb-4 text-xl font-bold">
      Create Task
    </h2>

    <div class="flex flex-col gap-3 md:flex-row">
      <input
        v-model="title"
        type="text"
        placeholder="Task title"
        class="flex-1 rounded-lg border border-gray-300 px-4 py-2 outline-none focus:border-blue-500"
        @keyup.enter="handleSubmit"
      />

      <input
        v-model="description"
        type="text"
        placeholder="Description"
        class="flex-1 rounded-lg border border-gray-300 px-4 py-2 outline-none focus:border-blue-500"
        @keyup.enter="handleSubmit"
      />

      <button
        type="button"
        class="rounded-lg bg-blue-600 px-5 py-2 text-white disabled:cursor-not-allowed disabled:bg-gray-300 disabled:text-gray-500"
        :disabled="!isFormValid"
        @click="handleSubmit"
      >
        Add Task
      </button>
    </div>
  </div>
</template>