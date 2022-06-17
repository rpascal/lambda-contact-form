build-ContactFormFunction:
	set -x
	sh ./buildArtifactOnly.sh
	cp ./bootstrap $(ARTIFACTS_DIR)/bootstrap
