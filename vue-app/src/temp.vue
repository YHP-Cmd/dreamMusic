<template>
{{user.username}}
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useUserStore } from "./stores/user.js"; // 修正拼写
import axios from "axios";
import config from './config.js'

const userStore = useUserStore(); // 修正拼写
const user = ref({});

// 获取数据
const getData = () => {
  const token = userStore.token; // 确保获取 token
  console.log("Token:", token);
  axios.get(`${config.api}/users/getUser`, {
    headers: {
      'Authorization':`Bearer ${token}`, // 确保 Authorization 前面是 'Bearer '
    }
  }).then(response => {
    if (response.data) { // 确保数据是数组
      console.log("Response Data:", response.data);
      user.value = response.data; // 将数据绑定到 users
    } else {
      console.error("Unexpected data format:", response.data);
    }
  }).catch(error => {
    console.error("Error fetching data:", error);
  });
}

onMounted(() => {
  getData(); // 组件加载时调用
});
</script>

<style scoped>
/* 你可以在这里添加样式 */
</style>
