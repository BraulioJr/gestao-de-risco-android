#!/usr/bin/env kotlin
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-text:0.3.0")

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File
import java.io.IOException
import java.util.Scanner

fun main() {
    try {
        val gitLog = executeCommand("git log --pretty=%s")
        val commits = gitLog.split("\n")
            .filter { it.isNotEmpty() }
            .mapNotNull { parseCommitMessage(it) }

        val features = commits.filter { it.type == "feat" }
        val fixes = commits.filter { it.type == "fix" }

        val changelogContent = StringBuilder()
        changelogContent.append("# Changelog\n\n")
        changelogContent.append("## [Unreleased]\n")

        if (features.isNotEmpty()) {
            changelogContent.append("### Added\n")
            features.forEach {
                changelogContent.append("- ${it.scope}: ${it.description}\n")
            }
        }

        if (fixes.isNotEmpty()) {
            changelogContent.append("### Fixed\n")
            fixes.forEach {
                changelogContent.append("- ${it.scope}: ${it.description}\n")
            }
        }

        File("CHANGELOG.md").writeText(changelogContent.toString())

        println("CHANGELOG.md gerado com sucesso!")
    } catch (e: IOException) {
        println("Erro ao executar comando git: ${e.message}")
    } catch (e: Exception) {
        println("Erro ao gerar CHANGELOG.md: ${e.message}")
    }
}

data class CommitInfo(val type: String, val scope: String, val description: String)

fun parseCommitMessage(message: String): CommitInfo? {
    val pattern = "^(feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert)(\\((.*?)\\))?: (.*)$".toRegex()
    val matchResult = pattern.find(message) ?: return null

    val (type, _, scope, description) = matchResult.destructured
    return CommitInfo(type, scope, description)
}

fun executeCommand(command: String): String {
    return try {
        val process = ProcessBuilder(*command.split(" ").toTypedArray())
            .directory(File("."))
            .redirectErrorStream(true)
            .start()

        val output = StringBuilder()
        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { output.append(it).append("\n") }
        }

        process.waitFor()

        if (process.exitValue() != 0) {
            throw IOException("Command '$command' failed with exit code ${process.exitValue()}")
        }

        output.toString()
    } catch (e: IOException) {
        throw e
    } catch (e: Exception) {
        throw e
    }
}

fun extractScope(message: String): String {
    val scopeRegex = "\\((.*?)\\)".toRegex()
    val matchResult = scopeRegex.find(message)
    return matchResult?.groups?.get(1)?.value ?: ""
}