<script setup>
import { ref, watch } from "vue";

const props = defineProps({
  task: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits([
  "save-task",
  "toggle-task",
  "delete-task",
]);

const isEditing = ref(false);

const editForm = ref({
  title: "",
  description: "",
});

/* [AI assisted chat-008]
 * 當父層 task 更新時，同步更新 editForm。
 * immediate: true 讓元件第一次建立時也會立即執行。
 */
watch(
  () => props.task,
  (task) => {
    if (!task) {
      return;
    }

    editForm.value = {
      title: task.title ?? "",
      description: task.description ?? "",
    };
  },
  {
    immediate: true,
    deep: true,
  }
);

const startEdit = () => {
  editForm.value = {
    title: props.task.title ?? "",
    description: props.task.description ?? "",
  };

  isEditing.value = true;
};

const cancelEdit = () => {
  editForm.value = {
    title: props.task.title ?? "",
    description: props.task.description ?? "",
  };

  isEditing.value = false;
};

const handleSave = () => {
  const title = editForm.value.title.trim();

  if (!title) {
    return;
  }

  emit("save-task", {
    id: props.task.id,
    title,
    description: editForm.value.description.trim(),
    completed: props.task.completed,
  });

  isEditing.value = false;
};

const handleToggle = () => {
  emit("toggle-task", props.task);
};

const handleDelete = () => {
  emit("delete-task", props.task.id);
};
</script>

<template>
  <article class="rounded-xl bg-white p-6 shadow">
    <!-- 顯示模式 -->
    <template v-if="!isEditing">
      <div class="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div>
          <h3
            class="text-xl font-bold"
            :class="{ 'text-gray-400 line-through': task.completed }"
          >
            {{ task.title }}
          </h3>

          <p
            v-if="task.description"
            class="mt-2 text-gray-500"
          >
            {{ task.description }}
          </p>
        </div>

        <span
          class="w-fit rounded-full px-3 py-1 text-sm font-medium"
          :class="
            task.completed
              ? 'bg-green-100 text-green-700'
              : 'bg-yellow-100 text-yellow-700'
          "
        >
          {{ task.completed ? "Completed" : "Pending" }}
        </span>
      </div>

      <div class="mt-5 flex flex-wrap gap-2">
        <button
          type="button"
          class="rounded-lg bg-indigo-500 px-4 py-2 text-white transition hover:bg-indigo-600"
          @click="startEdit"
        >
          Edit
        </button>

        <button
          type="button"
          class="rounded-lg bg-green-500 px-4 py-2 text-white transition hover:bg-green-600"
          @click="handleToggle"
        >
          {{ task.completed ? "Undo" : "Complete" }}
        </button>

        <button
          type="button"
          class="rounded-lg bg-red-500 px-4 py-2 text-white transition hover:bg-red-600"
          @click="handleDelete"
        >
          Delete
        </button>
      </div>
    </template>

    <!-- 編輯模式 -->
    <template v-else>
      <div class="space-y-3">
        <input
          v-model="editForm.title"
          type="text"
          placeholder="Task title"
          class="w-full rounded-lg border border-gray-300 p-3 outline-none focus:border-blue-500"
        />

        <textarea
          v-model="editForm.description"
          rows="3"
          placeholder="Description"
          class="w-full resize-none rounded-lg border border-gray-300 p-3 outline-none focus:border-blue-500"
        />

        <div class="flex gap-2">
          <button
            type="button"
            class="rounded-lg bg-blue-600 px-4 py-2 text-white transition hover:bg-blue-700"
            @click="handleSave"
          >
            Save
          </button>

          <button
            type="button"
            class="rounded-lg bg-gray-500 px-4 py-2 text-white transition hover:bg-gray-600"
            @click="cancelEdit"
          >
            Cancel
          </button>
        </div>
      </div>
    </template>
  </article>
</template>