<script setup>
import { ref, onMounted } from "vue";
import {
  getTasks,
  createTask,
  updateTask,
  deleteTask,
} from "./api/taskApi";

const tasks = ref([]);

const title = ref("");
const description = ref("");

const editingId = ref(null);

const editForm = ref({
  title: "",
  description: "",
});

// 檢視任務
const loadTasks = async () => {
  const res = await getTasks();
  tasks.value = res.data;
};

// 新增任務
const addTask = async () => {
  if (!title.value) return;

  await createTask({
    title: title.value,
    description: description.value,
  });

  title.value = "";
  description.value = "";

  loadTasks();
};

// 修改任務
const startEdit = (task) => {
  editingId.value = task.id;

  editForm.value = {
    title: task.title,
    description: task.description,
  };
};

// 取消編輯
const cancelEdit = () => {
  editingId.value = null;
};

// 儲存任務
const saveTask = async (task) => {
  const res = await updateTask(task.id, {
    ...task,
    title: editForm.value.title,
    description: editForm.value.description,
  });

  const index = tasks.value.findIndex(
    (t) => t.id === task.id
  );

  tasks.value[index] = res.data;
  editingId.value = null;
};

// 切換任務狀態
const toggleTask = async (task) => {
  const res = await updateTask(task.id, {
    ...task,
    completed: !task.completed,
  });

  const index = tasks.value.findIndex(
    (t) => t.id === task.id
  );
  
  tasks.value[index] = res.data;
};

// 刪除任務
const removeTask = async (id) => {
  await deleteTask(id);
  loadTasks();
};


onMounted(loadTasks);
</script>

<template>
  <div class="min-h-screen bg-slate-100">
    <div class="max-w-6xl mx-auto p-8">
      <h1 class="text-4xl font-bold mb-8">
        Task Manager Dashboard
      </h1>
      <!-- 統計卡片 -->
      <div class="grid grid-cols-3 gap-4 mb-8">
        <div class="bg-white rounded-xl p-6 shadow">
          <p class="text-gray-500">Total Tasks</p>
          <h2 class="text-3xl font-bold">
            {{ tasks.length }}
          </h2>
        </div>
        <div class="bg-white rounded-xl p-6 shadow">
          <p class="text-gray-500">Completed</p>
          <h2 class="text-3xl font-bold text-green-600">
            {{ tasks.filter(t => t.completed).length }}
          </h2>
        </div>
        <div class="bg-white rounded-xl p-6 shadow">
          <p class="text-gray-500">Pending</p>
          <h2 class="text-3xl font-bold text-orange-500">
            {{ tasks.filter(t => !t.completed).length }}
          </h2>
        </div>
      </div>
      <!-- 新增任務 -->
      <div class="bg-white rounded-xl shadow p-6 mb-8">
        <h2 class="font-bold text-xl mb-4">
          Create Task
        </h2>
        <div class="flex gap-3">
          <input
            v-model="title"
            placeholder="Task title"
            class="border rounded-lg px-4 py-2 flex-1"
          />
          <input
            v-model="description"
            placeholder="Description"
            class="border rounded-lg px-4 py-2 flex-1"
          />
          <button
            @click="addTask"
            class="bg-blue-600 text-white px-5 rounded-lg hover:bg-blue-700"
          >
            Add
          </button>
        </div>
      </div>
      <div
        class="bg-white rounded-xl shadow p-6 mb-4"
        v-for="task in tasks"
        :key="task?.id"
      >
        <!-- 顯示模式 -->
        <template v-if="task && editingId !== task.id">
          <div class="flex justify-between items-center">
            <div>
              <h3 class="font-bold text-xl">
                {{ task.title }}
              </h3>
              <p class="text-gray-500 mt-2">
                {{ task.description }}
              </p>
            </div>
            <span
              class="px-3 py-1 rounded-full text-sm"
              :class="
                task.completed
                  ? 'bg-green-100 text-green-700'
                  : 'bg-yellow-100 text-yellow-700'
              "
            >
              {{ task.completed ? 'Completed' : 'Pending' }}
            </span>
          </div>
          <div class="flex gap-2 mt-5">
            <button
              @click="startEdit(task)"
              class="bg-indigo-500 text-white px-4 py-2 rounded-lg"
            >
              Edit
            </button>
            <button
              @click="toggleTask(task)"
              class="bg-green-500 text-white px-4 py-2 rounded-lg"
            >
              Toggle
            </button>
            <button
              @click="removeTask(task.id)"
              class="bg-red-500 text-white px-4 py-2 rounded-lg"
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
              class="w-full border rounded-lg p-3"
            />
            <textarea
              v-model="editForm.description"
              class="w-full border rounded-lg p-3"
            />
            <div class="flex gap-2">
              <button
                @click="saveTask(task)"
                class="bg-blue-600 text-white px-4 py-2 rounded-lg"
              >
                Save
              </button>
              <button
                @click="cancelEdit"
                class="bg-gray-500 text-white px-4 py-2 rounded-lg"
              >
                Cancel
              </button>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>