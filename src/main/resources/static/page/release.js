$(function () {
	$("#btn-download-all").click(function() {
		var release = false;
		if (confirm("현재 버전을 배포 이력에 기록하시겠습니까?")) {
			release = true;
		}
		$(".spinner-border").show();
		$("#ifrm").attr("src", "/api/v1/ecm/release/all?release=" + release);
		setTimeout(function() {
			$(".spinner-border").hide();
		}, 6000);
	});
});

function downloadFromHistory(id) {
	if (confirm("해당 버전을 다운로드 하시겠습니까?")) {
	$(".spinner-border").show();
		$("#ifrm").attr("src", "/api/v1/ecm/release/all/" + id);
		setTimeout(function() {
			$(".spinner-border").hide();
		}, 6000);
	}
}