<template>
  <div class="progress-circle-container">
    <svg class="progress-circle" width="120" height="120" viewBox="0 0 120 120">
      <circle 
        class="progress-circle-bg" 
        cx="60" cy="60" r="54" 
        fill="none" 
        stroke="#e2e8f0" 
        stroke-width="12"
      />
      <circle 
        class="progress-circle-value" 
        cx="60" cy="60" r="54" 
        fill="none" 
        :stroke="error ? '#f56565' : '#3182ce'" 
        stroke-width="12"
        stroke-dasharray="339.292" 
        :stroke-dashoffset="dashOffset"
        stroke-linecap="round"
      />
    </svg>
    <div class="progress-text">
      {{ displayProgress }}%
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  progress: {
    type: Number,
    default: 0
  },
  error: {
    type: Boolean,
    default: false
  }
});

const displayProgress = computed(() => Math.round(props.progress));
const dashOffset = computed(() => {
  const circumference = 2 * Math.PI * 54;
  return circumference - (circumference * props.progress) / 100;
});
</script>

<style scoped>
.progress-circle-container {
  position: relative;
  display: inline-flex;
  justify-content: center;
  align-items: center;
}

.progress-circle {
  transform: rotate(-90deg);
  transition: all 0.3s ease;
}

.progress-circle-value {
  transition: stroke-dashoffset 0.3s ease;
}

.progress-text {
  position: absolute;
  font-size: 1.5rem;
  font-weight: bold;
  color: #4a5568;
}
</style>
