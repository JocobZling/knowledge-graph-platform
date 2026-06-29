<template>
  <section class="page">
    <div class="page-title"><h1>时间轴</h1><el-button @click="load">刷新</el-button></div>
    <div class="panel">
      <el-timeline>
        <el-timeline-item v-for="item in rows" :key="item.id" :timestamp="item.eventDate" :type="item.eventType === 'milestone' ? 'primary' : 'info'">
          <strong>{{ item.title }}</strong>
          <p>{{ item.content }}</p>
        </el-timeline-item>
      </el-timeline>
    </div>
  </section>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import http from '../api/http'
const rows = ref([])
async function load() { rows.value = await http.get('/timeline/list') }
onMounted(load)
</script>
