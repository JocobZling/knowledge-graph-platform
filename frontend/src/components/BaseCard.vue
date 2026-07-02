<template>
  <section class="base-card" :class="{ 'base-card--compact': compact }">
    <div v-if="title || $slots.header" class="base-card__header">
      <div>
        <h3 v-if="title">{{ title }}</h3>
        <p v-if="description">{{ description }}</p>
      </div>
      <slot name="header" />
    </div>
    <slot />
  </section>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' },
  description: { type: String, default: '' },
  compact: { type: Boolean, default: false }
})
</script>
