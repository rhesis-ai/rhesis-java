.PHONY: format lint check test test-unit test-integration build clean docker-up docker-down docker-clean

# Format code using spotless (fixes the formatting errors you're seeing)
format:
	mvn spotless:apply

# Check code formatting and static analysis
lint:
	mvn spotless:check pmd:check

check: lint

# Run all tests (unit and integration) (disables PMD checking since it's handled by lint)
test:
	mvn verify -Dpmd.skip=true

# Run only unit tests
test-unit:
	mvn test -Dpmd.skip=true

# Run only integration tests (disables PMD checking since it's handled by lint)
test-integration:
	mvn verify -Dsurefire.skip=true -Dpmd.skip=true

# Build the project
build:
	mvn clean install

# Clean the target directory
clean:
	mvn clean

# Docker management for integration tests
docker-up:
	docker compose -f docker-compose.test.yml up -d --wait
	@echo "Seeding project scope on test token..."
	@docker compose -f docker-compose.test.yml exec -T test-postgres \
		psql -U rhesis-user -d rhesis-db -c \
		"UPDATE tokens SET project_id = (SELECT id FROM projects LIMIT 1);"

docker-down:
	docker compose -f docker-compose.test.yml down

docker-clean:
	docker compose -f docker-compose.test.yml down -v
