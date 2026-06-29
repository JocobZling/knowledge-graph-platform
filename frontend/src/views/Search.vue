<template>
  <section class="page">
    <div class="page-title"><h1>全局搜索</h1></div>
    <div class="toolbar"><el-input v-model="keyword" placeholder="输入关键词" @keyup.enter="search" /><el-button type="primary" @click="search">搜索</el-button></div>
    <div v-for="(items, key) in result" :key="key" class="panel">
      <h3>{{ key }}</h3>
      <el-table :data="items">
        <el-table-column prop="title" label="标题/名称">
          <template #default="{ row }">{{ row.title || row.name }}</template>
        </el-table-column>
        <el-table-column prop="summary" label="摘要" />
        <el-table-column prop="status" label="状态" width="120" />
      </el-table>
    </div>
  </section>
</template>
<script setup>
import { ref } from 'vue'
import http from '../api/http'
const keyword = ref('')
const result = ref({})
async function search() { if (keyword.value) result.value = await http.get('/search', { params: { keyword: keyword.value } }) }
</script>
