<script setup>
import { computed } from 'vue';
import { cn } from '@/lib/utils';

const props = defineProps({
  speed: {
    type: Number,
    default: 1,
  },
  colorFrom: {
    type: String,
    default: '#ffaa40',
  },
  colorTo: {
    type: String,
    default: '#9c40ff',
  },
  className: {
    type: String,
    default: '',
  },
});

const style = computed(() => ({
  '--bg-size': `${props.speed * 300}%`,
  '--color-from': props.colorFrom,
  '--color-to': props.colorTo,
}));
</script>

<template>
  <span
    :style="style"
    :class="cn(
      `inline animate-gradient bg-gradient-to-r from-[var(--color-from)] via-[var(--color-to)] to-[var(--color-from)] bg-[length:var(--bg-size)_100%] bg-clip-text text-transparent`,
      className,
    )"
  >
    <slot></slot>
  </span>
</template>
