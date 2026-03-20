# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.1] - 2026-03-20

### Added
- Implemented File API capabilities (`FileUpload`, multipart uploads) matching the Python SDK.
- Support for attaching files to `Test` and `TestSet` entities.
- Added overloaded convenience methods to directly accept `Path`, `java.io.File`, and base64 strings for file upload.
- Added `Lombok` dependency (`@Builder`) to eliminate boilerplate in SDK entities.

### Changed
- Refactored all entity classes (`Test`, `TestSet`, `Endpoint`, `Project`, etc.) to use the Builder pattern instead of verbose constructors.
- Updated `CreateEndpointExample` and `FileSupportExample` to use the new Builder syntax.
- Updated unit tests (`EntityTest`, `ClientWiremockTest`) and integration tests to use the new entity builders.
- Fixed a Jackson deserialization issue using `NameStringDeserializer` to handle polymorphic JSON fields from the backend API.

## [0.1.0] - 2026-03-18

### Added
- Initial release of the Rhesis Java SDK.
- Core entities and API clients (`Test`, `TestSet`, `Endpoint`, `Project`, `TestRun`, `TestResult`, `Prompt`, `Status`).
- Local synthesizers for test generation using Jinja templates.
- Comprehensive unit testing suite using WireMock, JUnit 5, and AssertJ.
- Integration tests setup using Maven Failsafe plugin.
- Support for loading environment variables via `dotenv-java`.
- CI/CD workflows for linting (Spotless, PMD, ErrorProne) and testing on pull requests.
- Makefile for common build tasks.
- GitHub Packages publishing setup.
