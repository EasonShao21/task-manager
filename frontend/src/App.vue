<script setup>
import { ref, onMounted, computed } from "vue";

import TaskForm from "./components/TaskForm.vue";
import TaskList from "./components/TaskList.vue";

import {
  getTasks,
  createTask,
  updateTask,
  deleteTask,
} from "./api/taskApi";

const tasks = ref([]);
const loading = ref(false);
const errorMessage = ref("");

const filterStatus = ref("all");

const totalTasks = computed(() => tasks.value.length);

const completedTasks = computed(
  () => tasks.value.filter((task) => task.completed).length
);

const pendingTasks = computed(
  () => tasks.value.filter((task) => !task.completed).length
);

// 查詢任務
const loadTasks = async () => {
  loading.value = true;
  errorMessage.value = "";

  try {
    const res = await getTasks();

    tasks.value = Array.isArray(res.data)
      ? res.data
      : [];
  } catch (error) {
    console.error("載入任務失敗：", error);
    errorMessage.value = "無法載入任務資料";
  } finally {
    loading.value = false;
  }
};

// 新增任務
const addTask = async (formData) => {
  errorMessage.value = "";

  try {
    const res = await createTask({
      title: formData.title,
      description: formData.description,
      completed: false,
    });

    /*
     * 直接把後端建立完成的 Task 加到陣列最後，
     * 不重新 loadTasks，因此順序不會被重新排列。
     */
    tasks.value.push(res.data);
  } catch (error) {
    console.error("新增任務失敗：", error);
    errorMessage.value = "新增任務失敗";
  }
};

// 修改任務
const saveTask = async (updatedTask) => {
  errorMessage.value = "";

  try {
    const res = await updateTask(
      updatedTask.id,
      updatedTask
    );

    /*
     * 找到原本的 index，只替換該筆資料。
     * 避免 PUT 完重新 GET 全部資料而改變畫面順序。
     */
    const index = tasks.value.findIndex(
      (task) => task.id === updatedTask.id
    );

    if (index !== -1) {
      tasks.value[index] = res.data;
    }
  } catch (error) {
    console.error("修改任務失敗：", error);
    errorMessage.value = "修改任務失敗";
  }
};

// 切換完成狀態
const toggleTask = async (task) => {
  errorMessage.value = "";

  if (!task) {
    return;
  }

  try {
    const res = await updateTask(task.id, {
      title: task.title,
      description: task.description,
      completed: !task.completed,
    });

    /*
     * 一樣只更新原本 index，
     * 因此 Toggle 後 Task 不會移動位置。
     */
    const index = tasks.value.findIndex(
      (item) => item.id === task.id
    );

    if (index !== -1) {
      tasks.value[index] = res.data;
    }
  } catch (error) {
    console.error("切換任務狀態失敗：", error);
    errorMessage.value = "更新任務狀態失敗";
  }
};

// 刪除任務
const removeTask = async (id) => {
  errorMessage.value = "";

  try {
    await deleteTask(id);

    /*
     * 刪除成功後直接從前端陣列移除，
     * 不需要重新打 GET API。
     */
    tasks.value = tasks.value.filter(
      (task) => task.id !== id
    );
  } catch (error) {
    console.error("刪除任務失敗：", error);
    errorMessage.value = "刪除任務失敗";
  }
};

// 篩選任務
const filteredTasks = computed(() => {
  if (filterStatus.value === "completed") {
    return tasks.value.filter((task) => task.completed);
  }
  if (filterStatus.value === "pending") {
    return tasks.value.filter((task) => !task.completed);
  }
  return tasks.value;
});

onMounted(loadTasks);
</script>

<template>
  <main class="min-h-screen bg-slate-100">
    <div class="mx-auto max-w-6xl p-6 md:p-8">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 md:text-4xl">
          Task Manager Dashboard
        </h1>

        <p class="mt-2 text-gray-500">
          Manage your tasks and track progress.
        </p>
      </div>

      <!-- 統計區 -->
      <section class="mb-8 grid gap-4 md:grid-cols-3">
        <div class="rounded-xl bg-white p-6 shadow">
          <p class="text-sm text-gray-500">
            Total Tasks
          </p>

          <p class="mt-2 text-3xl font-bold">
            {{ totalTasks }}
          </p>
        </div>

        <div class="rounded-xl bg-white p-6 shadow">
          <p class="text-sm text-gray-500">
            Completed
          </p>

          <p class="mt-2 text-3xl font-bold text-green-600">
            {{ completedTasks }}
          </p>
        </div>

        <div class="rounded-xl bg-white p-6 shadow">
          <p class="text-sm text-gray-500">
            Pending
          </p>

          <p class="mt-2 text-3xl font-bold text-orange-500">
            {{ pendingTasks }}
          </p>
        </div>
      </section>

      <!-- 新增 -->
      <TaskForm
        @add-task="addTask"
      />
      <div class="mt-3 h-20 flex items-center justify-end">
        <select
          v-model="filterStatus"
          class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm text-gray-700
                focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
        >
          <option value="all">All</option>
          <option value="completed">Completed</option>
          <option value="pending">Pending</option>
        </select>
      </div>

      <!-- API Error -->
      <div
        v-if="errorMessage"
        class="mb-6 rounded-lg bg-red-100 px-4 py-3 text-red-700"
      >
        {{ errorMessage }}
      </div>

      <!-- Loading -->
      <div
        v-if="loading"
        class="rounded-xl bg-white p-10 text-center text-gray-500 shadow"
      >
        Loading...
      </div>

      <!-- Task List -->
      <TaskList
        v-else
        :tasks="filteredTasks"
        @save-task="saveTask"
        @toggle-task="toggleTask"
        @delete-task="removeTask"
      />
    </div>
  </main>
</template>