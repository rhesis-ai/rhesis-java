.PHONY: format lint check test test-unit test-integration build clean

# Format code using spotless (fixes the formatting errors you're seeing)
format:
	mvn spotless:apply

# Check code formatting and static analysis
lint:
	mvn spotless:check pmd:check

check: lint

# Run all tests (unit and integration)
test:
	mvn verify

# Run only unit tests
test-unit:
	mvn test

# Run only integration tests
test-integration:
	mvn verify -Dsurefire.skip=true

# Build the project
build:
	mvn clean install

# Clean the target directory
clean:
	mvn clean
