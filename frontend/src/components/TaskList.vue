<script setup>
import TaskCard from "./TaskCard.vue";

// [AI assisted chat-008]
// 使用 AI 協助將任務列表拆成獨立元件
// TaskList 只負責列表渲染，實際操作事件再往父元件傳遞

defineProps({
  tasks: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits([
  "save-task",
  "toggle-task",
  "delete-task",
]);

const handleSave = (task) => {
  emit("save-task", task);
};

const handleToggle = (task) => {
  emit("toggle-task", task);
};

const handleDelete = (id) => {
  emit("delete-task", id);
};
</script>

<template>
  <div>
    <div
      v-if="tasks.length === 0"
      class="rounded-xl bg-white p-10 text-center text-gray-500 shadow"
    >
      目前沒有任務
    </div>

    <div
      v-else
      class="space-y-4"
    >
      <TaskCard
        v-for="task in tasks"
        :key="task.id"
        :task="task"
        @save-task="handleSave"
        @toggle-task="handleToggle"
        @delete-task="handleDelete"
      />
    </div>
  </div>
</template>