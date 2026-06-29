<template>
  <section class="page">
    <div class="page-title"><h1>知识图谱</h1><el-button @click="load">刷新</el-button></div>
    <div class="panel"><div ref="chartRef" class="graph"></div></div>
  </section>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import http from '../api/http'
const chartRef = ref()
let chart
async function load() {
  const data = await http.get('/graph/all')
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
      data: data.nodes.map((n) => ({ ...n, category: n.type, symbolSize: n.type === 'project' ? 62 : 48 })),
      links: data.links
    }]
  })
}
onMounted(load)
</script>
