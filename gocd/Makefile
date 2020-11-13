.PHONY: help
help:
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / { printf "\033[36m%-30s\033[0m %s\n", $$1, $$2 }' $(MAKEFILE_LIST)
.DEFAULT_GOAL := help

# DOCKER TASKS
start: ## Run servers to develop test
	mkdir -p ./plugins/external
	cp ../build/libs/*.jar ./plugins/external
	docker-compose up -d

stop: ## Stop servers
	rm -rf ./plugins
	docker-compose down
