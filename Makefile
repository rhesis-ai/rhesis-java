.PHONY: format lint check test build clean

# Format code using spotless (fixes the formatting errors you're seeing)
format:
	mvn spotless:apply

# Check code formatting
lint:
	mvn spotless:check

check: lint

# Run tests
test:
	mvn test

# Build the project
build:
	mvn clean install

# Clean the target directory
clean:
	mvn clean
