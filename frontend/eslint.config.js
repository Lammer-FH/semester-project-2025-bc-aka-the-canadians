import js from '@eslint/js'
import globals from 'globals'
import tseslint from '@typescript-eslint/eslint-plugin'
import tsparser from '@typescript-eslint/parser'
import pluginVue from 'eslint-plugin-vue'
import vueParser from 'vue-eslint-parser'

export default [
  // Base ESLint recommended rules
  js.configs.recommended,
  
  // Vue recommended rules for Vue 3
  ...pluginVue.configs['flat/recommended'],
  
  {
    files: ['**/*.{js,mjs,cjs,ts,tsx,vue}'],
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.node,
        ...globals.es2022,
      },
      ecmaVersion: 2022,
      sourceType: 'module',
    },
    plugins: {
      '@typescript-eslint': tseslint,
      vue: pluginVue,
    },
    rules: {
      // TypeScript rules
      '@typescript-eslint/no-unused-vars': [
        'error',
        { argsIgnorePattern: '^_' },
      ],
      '@typescript-eslint/no-explicit-any': 'warn',

      // Relax Vue formatting rules for better DX
      'vue/html-self-closing': 'off',
      'vue/max-attributes-per-line': 'off',
      'vue/singleline-html-element-content-newline': 'off',
      'vue/multiline-html-element-content-newline': 'off',
      'vue/html-closing-bracket-newline': 'off',
      'vue/html-indent': 'off',
      'vue/attributes-order': 'off',
      'vue/attribute-hyphenation': 'off',
      'vue/v-on-event-hyphenation': 'off',

      // Warnings for deprecated features - but allow slot on Ionic components
      'vue/no-deprecated-slot-attribute': [
        'warn',
        {
          ignore: [
            // Layout components
            'ion-header', 'ion-footer', 'ion-content', 'ion-toolbar', 'ion-buttons',
            // Interactive components
            'ion-button', 'ion-fab-button', 'ion-tab-button', 'ion-segment-button',
            // Form components
            'ion-input', 'ion-textarea', 'ion-select', 'ion-checkbox', 'ion-radio', 'ion-toggle', 'ion-range', 'ion-searchbar',
            // Display components
            'ion-item', 'ion-label', 'ion-icon', 'ion-chip', 'ion-badge', 'ion-avatar', 'ion-thumbnail', 'ion-note',
            // Feedback components
            'ion-skeleton-text', 'ion-spinner', 'ion-progress-bar',
            // List components
            'ion-infinite-scroll-content', 'ion-refresher-content', 'ion-reorder', 'ion-slide'
          ]
        }
      ],
    },
  },
  
  // TypeScript-specific configuration
  {
    files: ['**/*.ts', '**/*.tsx'],
    languageOptions: {
      parser: tsparser,
      parserOptions: {
        ecmaVersion: 2022,
        sourceType: 'module',
      },
    },
    plugins: {
      '@typescript-eslint': tseslint,
    },
    rules: {
      ...tseslint.configs.recommended.rules,
      '@typescript-eslint/no-unused-vars': [
        'error',
        { argsIgnorePattern: '^_' },
      ],
      '@typescript-eslint/no-explicit-any': 'warn',
    },
  },
  
  // Vue-specific configuration
  {
    files: ['**/*.vue'],
    languageOptions: {
      parser: vueParser,
      parserOptions: {
        parser: tsparser,
        ecmaVersion: 2022,
        sourceType: 'module',
      },
    },
  },
  
  // Files to ignore
  {
    ignores: [
      'dist/**',
      'build/**',
      'node_modules/**',
      '.env',
      '.env.local',
      '.env.*.local',
      '.cache/**',
      '.vite/**',
      'coverage/**',
      '*.tsbuildinfo',
      'android/**',
      'ios/**',
      'www/**',
      'platforms/**',
      'plugins/**',
      'resources/**',
    ],
  },
]