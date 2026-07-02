<template>
  <div class="markdown-preview" v-html="rendered"></div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  content: { type: String, default: '' }
})

const rendered = computed(() => renderMarkdown(props.content || ''))

function escapeHtml(value) {
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;')
}

function inlineMarkdown(value) {
  return escapeHtml(value)
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\[(.+?)\]\((.+?)\)/g, '<a href="$2" target="_blank" rel="noreferrer">$1</a>')
    .replace(/\`(.+?)\`/g, '<code>$1</code>')
}

function renderMarkdown(markdown) {
  const lines = markdown.replace(/\r\n/g, '\n').split('\n')
  const html = []
  let inList = false
  let inCode = false
  let codeLines = []

  const closeList = () => {
    if (inList) {
      html.push('</ul>')
      inList = false
    }
  }

  lines.forEach((line) => {
    if (line.startsWith('\`\`\`')) {
      if (inCode) {
        html.push('<pre><code>' + escapeHtml(codeLines.join('\n')) + '</code></pre>')
        codeLines = []
      } else {
        closeList()
      }
      inCode = !inCode
      return
    }

    if (inCode) {
      codeLines.push(line)
      return
    }

    if (!line.trim()) {
      closeList()
      return
    }

    if (line.startsWith('# ')) {
      closeList()
      html.push('<h1>' + inlineMarkdown(line.slice(2)) + '</h1>')
      return
    }

    if (line.startsWith('## ')) {
      closeList()
      html.push('<h2>' + inlineMarkdown(line.slice(3)) + '</h2>')
      return
    }

    if (line.startsWith('### ')) {
      closeList()
      html.push('<h3>' + inlineMarkdown(line.slice(4)) + '</h3>')
      return
    }

    if (line.startsWith('> ')) {
      closeList()
      html.push('<blockquote>' + inlineMarkdown(line.slice(2)) + '</blockquote>')
      return
    }

    if (line.startsWith('- ')) {
      if (!inList) {
        html.push('<ul>')
        inList = true
      }
      html.push('<li>' + inlineMarkdown(line.slice(2)) + '</li>')
      return
    }

    closeList()
    html.push('<p>' + inlineMarkdown(line) + '</p>')
  })

  closeList()
  return html.join('')
}
</script>
