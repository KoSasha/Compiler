	.text
	.global main
main:
.LFB0:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq $16, %rsp
	movl $20, -12(%rbp)
	movl $5, -8(%rbp)
	movl  -8(%rbp), %edx
	movl  -12(%rbp), %eax
	movl %edx, %esi
	movl %eax, %edi
	call gdc
	movl	%eax, -4(%rbp)
	movl	-4(%rbp), %eax
	movl	%eax, %esi
	leaq	.LC0(%rip), %rdi
	movl	$0, %eax
	call	printf@PLT
	movl	$0, %eax
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LC0:
	.string	"%d\n"
	.text
	.global gdc
gdc:
.LFB1:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movl  %edi, -20(%rbp)
	movl  %esi, -24(%rbp)
	cmpl  $0, -20(%rbp)
	jne .L2
	movl $0, %eax
	jmp .L3
.L4:
	movl  -24(%rbp), %eax
	cmpl  -20(%rbp), %eax
	jge .L5
	movl  -24(%rbp), %eax
	movl  %eax, -4(%rbp)
	movl  -20(%rbp), %eax
	movl  %eax, -24(%rbp)
	movl  -4(%rbp), %eax
	movl  %eax, -20(%rbp)
.L5:
	movl  -24(%rbp), %eax
	cltd
	idivl  -20(%rbp)
	movl  %edx, -24(%rbp)
.L2:
	cmpl  $0, -24(%rbp)
	jne .L4
	movl -20(%rbp), %eax
.L3:
	popq  %rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
