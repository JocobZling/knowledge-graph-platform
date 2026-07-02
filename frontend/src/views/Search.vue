<template>
  <section class="page page-stack">
    <PageHeader title="全局搜索" eyebrow="Search" description="跨知识、问题、项目和 Prompt 检索已有内容。" />

    <BaseCard>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="输入关键词" clearable @keyup.enter="search" />
        <el-button type="primary" :loading="loading" @click="search">搜索</el-button>
      </div>
    </BaseCard>

    <EmptyState v-if="!hasResult && !loading" title="暂无搜索结果" description="输入关键词后，匹配内容会按类型分组展示。" />

    <BaseCard v-for="group in groups" :key="group.key" :title="group.key" v-loading="loading">
      <EmptyState v-if="!group.items.length" title="暂无匹配内容" description="换一个关键词再试试。" />
      <el-table v-else :data="group.items">
        <el-table-column prop="title" label="标题/名称">
          <template #default="{ row }">{{ row.title || row.name }}</template>
        </el-table-column>
        <el-table-column prop="summary" label="摘要" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <StatusTag :value="row.status" />
          </template>
        </el-table-column>
      </el-table>
    </BaseCard>
  </section>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import BaseCard from '../components/BaseCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import StatusTag from '../components/StatusTag.vue'
import http from '../api/http'

const keyword = ref('')
const result = ref({})
const loading = ref(false)
const groups = computed(() => Object.entries(result.value).map(([key, items]) => ({ key, items: items || [] })))
const hasResult = computed(() => groups.value.some((group) => group.items.length))

async function search() {
  if (!keyword.value.trim()) {
    result.value = {}
    return
  }
  loading.value = true
  try {
    result.value = await http.get('/search', { params: { keyword: keyword.value } })
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}
</script>
