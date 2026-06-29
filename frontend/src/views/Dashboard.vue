<template>
  <section class="page">
    <div class="page-title"><h1>Dashboard</h1></div>
    <div class="grid">
      <div class="metric">知识卡片<strong>{{ data.knowledgeCount || 0 }}</strong></div>
      <div class="metric">问题记录<strong>{{ data.issueCount || 0 }}</strong></div>
      <div class="metric">项目数量<strong>{{ data.projectCount || 0 }}</strong></div>
      <div class="metric">Prompt<strong>{{ data.promptCount || 0 }}</strong></div>
    </div>
    <div class="panel">
      <h3>最近问题</h3>
      <el-table :data="data.recentIssues || []">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="problemType" label="类型" width="160" />
      </el-table>
    </div>
    <div class="panel">
      <h3>进行中的项目</h3>
      <el-table :data="data.activeProjects || []">
        <el-table-column prop="name" label="项目" />
        <el-table-column prop="status" label="状态" width="140" />
        <el-table-column prop="priority" label="优先级" width="100" />
      </el-table>
    </div>
  </section>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'
const data = ref({})
onMounted(async () => { data.value = await http.get('/dashboard/summary') })
</script>
