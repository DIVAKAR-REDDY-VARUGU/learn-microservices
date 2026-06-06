#!/usr/bin/env bash
# Pretty test runner for a DSA question.
#   Usage (from the dsa/ folder):  bash run-tests.sh <category>/<subpattern>
#   Example:  bash run-tests.sh 01-arrays-hashing/02-hashing-previously-seen-existence-check
#
# Compiles Answer.java + Test.java, runs Test (which prints colored [PASS]/[FAIL]
# per case), then draws a banner + a pass-rate bar summary.

if [ -z "$1" ]; then
  echo "usage: bash run-tests.sh <category>/<subpattern>"
  exit 1
fi
DIR="$(cd "$(dirname "$0")" && pwd)/$1"
cd "$DIR" 2>/dev/null || { echo "no such folder: $1"; exit 1; }

# ANSI colors
G=$'\033[32m'; R=$'\033[31m'; Y=$'\033[33m'; C=$'\033[36m'; B=$'\033[1m'; D=$'\033[2m'; X=$'\033[0m'
RULE="══════════════════════════════════════════════════════════"

echo
echo "${C}${B}${RULE}${X}"
echo "${C}${B} 🧪  ${1}${X}"
echo "${C}${B}${RULE}${X}"

# ---- compile ----
if ! javac Answer.java Test.java 2>compile.err; then
  echo "${R}${B} ✗ COMPILE FAILED${X}"
  sed 's/^/   /' compile.err
  rm -f compile.err
  exit 1
fi
rm -f compile.err

# ---- run (stream live + capture for the summary) ----
echo
tmp="$(mktemp)"
java Test | tee "$tmp"

p=$(grep -c '\[PASS\]' "$tmp" 2>/dev/null); p=${p:-0}
f=$(grep -c '\[FAIL\]' "$tmp" 2>/dev/null); f=${f:-0}
rm -f "$tmp"
t=$((p + f))

echo
if [ "$t" -eq 0 ]; then
  echo "${Y}${B} no test cases detected${X}"
  exit 0
fi

# ---- pass-rate bar ----
pct=$(( p * 100 / t ))
width=28
filled=$(( p * width / t ))
empty=$(( width - filled ))
bar=""
i=0; while [ "$i" -lt "$filled" ]; do bar="${bar}█"; i=$((i + 1)); done
i=0; while [ "$i" -lt "$empty"  ]; do bar="${bar}░"; i=$((i + 1)); done

if [ "$f" -eq 0 ]; then col="$G"; tag="ALL PASSED ✓"; else col="$R"; tag="${f} FAILED ✗"; fi
echo "${B} Result ${X} ${col}${bar}${X}  ${B}${p}/${t}${X} ${D}(${pct}%)${X}  ${col}${B}${tag}${X}"
echo
