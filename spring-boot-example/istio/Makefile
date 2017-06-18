K8S_DIR = ../kubernetes
K8S_FILES = $(shell ls $(K8S_DIR)/*.yaml)
ISTIO_FILES = $(addprefix $(CURDIR)/, $(notdir $(K8S_FILES)))

all: $(ISTIO_FILES)

clean:
	rm -v *.yaml

$(CURDIR)/%.yaml: $(K8S_DIR)/%.yaml
	istioctl kube-inject -f $< -o $@

