<template>
  <section class="page page-stack">
    <PageHeader title="知识图谱" eyebrow="Graph" description="以关系图方式浏览知识、问题、项目和 Prompt 的连接。">
      <template #actions>
        <el-button :loading="loading" @click="load">刷新</el-button>
      </template>
    </PageHeader>

    <BaseCard v-loading="loading">
      <EmptyState v-if="!loading && empty" title="暂无图谱数据" description="建立知识关系后，图谱会显示在这里。" />
      <div v-show="!empty" ref="chartRef" class="graph"></div>
    </BaseCard>
  </section>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import BaseCard from '../components/BaseCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import http from '../api/http'

const chartRef = ref()
const loading = ref(false)
const empty = ref(false)
let chart

async function load() {
  loading.value = true
  try {
    const data = await http.get('/graph/all')
    empty.value = !data.nodes?.length
    await nextTick()
    if (empty.value) return
    chart ||= echarts.init(chartRef.value)
    chart.setOption({
      tooltip: {},
      legend: [{ data: ['knowledge', 'issue', 'project', 'prompt'] }],
      series: [{
        type: 'graph',
        layout: 'force',
        roam: true,
        label: { show: true },
        edgeLabel: { show: true, formatter: (p) => p.data.name },
        force: { repulsion: 220, edgeLength: 120 },
        categories: ['knowledge', 'issue', 'project', 'prompt'].map((name) => ({ name })),
        data: data.nodes.map((node) => ({ ...node, category: node.type, symbolSize: node.type === 'project' ? 62 : 48 })),
        links: data.links
      }]
    })
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
