<template>
  <section class="page page-stack">
    <PageHeader
      title="Dashboard"
      eyebrow="Overview"
      description="快速查看知识卡片、问题、项目和 Prompt 的整体状态。"
    />

    <div class="metric-grid" v-loading="loading">
      <BaseCard v-for="item in metrics" :key="item.label" class="metric-card" compact>
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </BaseCard>
    </div>

    <BaseCard title="最近问题" description="近期记录的问题与处理状态。" v-loading="loading">
      <EmptyState v-if="!loading && !recentIssues.length" title="暂无最近问题" description="新的问题记录会显示在这里。" />
      <el-table v-else :data="recentIssues">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <StatusTag :value="row.status" />
          </template>
        </el-table-column>
        <el-table-column prop="problemType" label="类型" width="160" />
      </el-table>
    </BaseCard>

    <BaseCard title="进行中的项目" description="仍在推进的项目与优先级。" v-loading="loading">
      <EmptyState v-if="!loading && !activeProjects.length" title="暂无进行中项目" description="活跃项目会显示在这里。" />
      <el-table v-else :data="activeProjects">
        <el-table-column prop="name" label="项目" />
        <el-table-column prop="status" label="状态" width="140">
          <template #default="{ row }">
            <StatusTag :value="row.status" />
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <StatusTag :value="row.priority" />
          </template>
        </el-table-column>
      </el-table>
    </BaseCard>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import BaseCard from '../components/BaseCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import StatusTag from '../components/StatusTag.vue'
import http from '../api/http'

const data = ref({})
const loading = ref(false)

const metrics = computed(() => [
  { label: '知识卡片', value: data.value.knowledgeCount || 0 },
  { label: '问题记录', value: data.value.issueCount || 0 },
  { label: '项目数量', value: data.value.projectCount || 0 },
  { label: 'Prompt', value: data.value.promptCount || 0 }
])
const recentIssues = computed(() => data.value.recentIssues || [])
const activeProjects = computed(() => data.value.activeProjects || [])

async function load() {
  loading.value = true
  try {
    data.value = await http.get('/dashboard/summary')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
