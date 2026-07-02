<template>
  <section class="page page-stack">
    <PageHeader title="时间轴" eyebrow="Timeline" description="按时间查看项目、知识和问题相关事件。">
      <template #actions>
        <el-button :loading="loading" @click="load">刷新</el-button>
      </template>
    </PageHeader>

    <BaseCard v-loading="loading">
      <EmptyState v-if="!loading && !rows.length" title="暂无时间轴事件" description="新的里程碑和记录会显示在这里。" />
      <el-timeline v-else>
        <el-timeline-item v-for="item in rows" :key="item.id" :timestamp="item.eventDate" :type="item.eventType === 'milestone' ? 'primary' : 'info'">
          <strong>{{ item.title }}</strong>
          <p>{{ item.content }}</p>
        </el-timeline-item>
      </el-timeline>
    </BaseCard>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import BaseCard from '../components/BaseCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import http from '../api/http'

const rows = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    rows.value = await http.get('/timeline/list')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
